// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.idea.maven.indices;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenRemoteRepository;
import org.jetbrains.idea.maven.onlinecompletion.model.MavenDependencyCompletionItem;
import org.jetbrains.idea.maven.onlinecompletion.model.MavenRepositoryArtifactInfo;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.project.MavenProjectsTree;
import org.jetbrains.idea.maven.server.NativeMavenProjectHolder;
import org.jetbrains.idea.maven.utils.MavenMergingUpdateQueue;
import org.jetbrains.idea.maven.utils.MavenSimpleProjectComponent;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.reposearch.DependencySearchService;
import org.jetbrains.idea.reposearch.SearchParameters;

import java.io.File;
import java.util.*;

public final class MavenProjectIndicesManager extends MavenSimpleProjectComponent implements Disposable {
  private volatile List<MavenIndex> myProjectIndices = new ArrayList<>();
  private @NotNull volatile List<MavenIndex> myLocalIndexes = Collections.emptyList();
  private final DependencySearchService myDependencySearchService;
  private final MergingUpdateQueue myUpdateQueue;

  public static MavenProjectIndicesManager getInstance(Project p) {
    return p.getService(MavenProjectIndicesManager.class);
  }

  @Override
  public void dispose() {
  }

  public MavenProjectIndicesManager(Project project) {
    super(project);
    myUpdateQueue = new MavenMergingUpdateQueue(getClass().getSimpleName(), 1000, true, this);
    myDependencySearchService = DependencySearchService.getInstance(project);

    if (!isNormalProject()) {
      return;
    }
    doInit();
  }

  public void doInit() {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      scheduleUpdateIndicesList();
    }

    MavenRepositoryProvider.EP_NAME.addChangeListener(this::scheduleUpdateIndicesList, this);

    getMavenProjectManager().addManagerListener(new MavenProjectsManager.Listener() {
      @Override
      public void activated() {
        scheduleUpdateIndicesList();
      }
    });

    getMavenProjectManager().addProjectsTreeListener(new MavenProjectsTree.Listener() {
      @Override
      public void projectsUpdated(@NotNull List<Pair<MavenProject, MavenProjectChanges>> updated, @NotNull List<MavenProject> deleted) {
        scheduleUpdateIndicesList();
      }

      @Override
      public void projectResolved(@NotNull Pair<MavenProject, MavenProjectChanges> projectWithChanges,
                                  NativeMavenProjectHolder nativeMavenProject) {
        scheduleUpdateIndicesList();
      }
    }, this);
  }

  private void scheduleUpdateIndicesList() {
    scheduleUpdateIndicesList(null);
  }

  public void scheduleUpdateIndicesList(@Nullable Consumer<? super List<MavenIndex>> consumer) {
    Update update = new Update(this) {
      @Override
      public void run() {
        Set<Pair<String, String>> remoteRepositoriesIdsAndUrls = ReadAction.compute(() -> {
          return myProject.isDisposed() ? null : collectRemoteRepositoriesIdsAndUrls();
        });
        File localRepository = ReadAction.compute(() -> myProject.isDisposed() ? null : getLocalRepository());
        if (remoteRepositoriesIdsAndUrls == null || localRepository == null || myProject.isDisposed()) {
          return;
        }

        List<MavenIndex> newProjectIndices;
        MavenIndicesManager mavenIndicesManager = MavenIndicesManager.getInstance(myProject);
        if (remoteRepositoriesIdsAndUrls.isEmpty()) {
          newProjectIndices = new ArrayList<>();
        }
        else {
          newProjectIndices = mavenIndicesManager.ensureIndicesExist(remoteRepositoriesIdsAndUrls);
        }
        ContainerUtil.addIfNotNull(newProjectIndices, mavenIndicesManager.createIndexForLocalRepo(myProject, localRepository));
        myDependencySearchService.updateProviders();

        myProjectIndices = newProjectIndices;
        myLocalIndexes = ContainerUtil.filter(myProjectIndices, mi -> mi.getKind() == MavenSearchIndex.Kind.LOCAL);
        if (consumer != null) {
          consumer.consume(myProjectIndices);
        }
      }
    };
    if (MavenUtil.isMavenUnitTestModeEnabled()) {
      update.run();
    }
    else {
      myUpdateQueue.queue(update);
    }
  }

  private File getLocalRepository() {
    return MavenProjectsManager.getInstance(myProject).getLocalRepository();
  }

  private Set<Pair<String, String>> collectRemoteRepositoriesIdsAndUrls() {
    Set<Pair<String, String>> result = new HashSet<>();
    Set<MavenRemoteRepository> remoteRepositories = new HashSet<>(getMavenProjectManager().getRemoteRepositories());
    for (MavenRepositoryProvider repositoryProvider : MavenRepositoryProvider.EP_NAME.getExtensions()) {
      remoteRepositories.addAll(repositoryProvider.getRemoteRepositories(myProject));
    }
    for (MavenRemoteRepository each : remoteRepositories) {
      String id = each.getId();
      String url = each.getUrl();

      result.add(Pair.create(id, url));
    }
    return result;
  }

  /**
   * @deprecated use {@link #getOfflineSearchService()}
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2021.3")
  public List<MavenIndex> getIndices() {
    return new ArrayList<>(myProjectIndices);
  }

  public void scheduleUpdateAll() {
    MavenIndicesManager.getInstance(myProject).scheduleUpdate(myProject, myProjectIndices);
  }

  public void scheduleUpdate(List<MavenIndex> indices) {
    MavenIndicesManager.getInstance(myProject).scheduleUpdate(myProject, indices);
  }

  public MavenIndicesManager.IndexUpdatingState getUpdatingState(MavenSearchIndex index) {
    return MavenIndicesManager.getInstance(myProject).getUpdatingState(index);
  }

  private MavenProjectsManager getMavenProjectManager() {
    return MavenProjectsManager.getInstance(myProject);
  }


  public synchronized DependencySearchService getDependencySearchService() {
    return myDependencySearchService;
  }

  @ApiStatus.Experimental
  public boolean hasRemotesExceptCentral() {
    return myProjectIndices.stream()
      .filter(i -> i.getKind() == MavenSearchIndex.Kind.REMOTE)
      .anyMatch(i -> !"central".equals(i.getRepositoryId()));
  }

  /**
   * @deprecated use {@link OfflineSearchService#findGroupCandidates} or{@link OfflineSearchService#findByTemplate} instead
   **/
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2021.3")
  public Set<String> getGroupIds() {
    return getGroupIds("");
  }

  /**
   * @deprecated use {@link OfflineSearchService#findGroupCandidates} or {@link OfflineSearchService#findByTemplate} instead
   **/
  @Deprecated
  public Set<String> getGroupIds(String pattern) {
    pattern = pattern == null ? "" : pattern;
    //todo fix
    Set<String> result = new HashSet<>();
    myDependencySearchService.fulltextSearch(pattern, new SearchParameters(true, true), it -> {
      if (it instanceof MavenRepositoryArtifactInfo) {
        result.add(((MavenRepositoryArtifactInfo)it).getGroupId());
      }
    });
    return result;
  }

  /**
   * @deprecated use {@link OfflineSearchService#findArtifactCandidates} or {@link OfflineSearchService#findByTemplate} instead
   **/
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2021.3")
  public Set<String> getArtifactIds(String groupId) {
    ProgressIndicatorProvider.checkCanceled();
    Set<String> result = new HashSet<>();
    myDependencySearchService.fulltextSearch(groupId + ":", new SearchParameters(true, true), it -> {
      if (it instanceof MavenRepositoryArtifactInfo) {
        if (StringUtil.equals(groupId, ((MavenRepositoryArtifactInfo)it).getGroupId())) {
          result.add(((MavenRepositoryArtifactInfo)it).getArtifactId());
        }
      }
    });
    return result;
  }

  /**
   * @deprecated use {@link OfflineSearchService#findAllVersions or {@link OfflineSearchService#findByTemplate} instead
   **/
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2021.3")
  public Set<String> getVersions(String groupId, String artifactId) {
    ProgressIndicatorProvider.checkCanceled();
    Set<String> result = new HashSet<>();
    myDependencySearchService.fulltextSearch(groupId + ":" + artifactId, new SearchParameters(true, true), it -> {
      if (it instanceof MavenRepositoryArtifactInfo) {
        if (StringUtil.equals(groupId, ((MavenRepositoryArtifactInfo)it).getGroupId()) &&
            StringUtil.equals(groupId, ((MavenRepositoryArtifactInfo)it).getArtifactId())) {
          for (MavenDependencyCompletionItem item : ((MavenRepositoryArtifactInfo)it).getItems()) {
            result.add(item.getVersion());
          }
        }
      }
    });
    return result;
  }

  public boolean hasLocalGroupId(String groupId) {
    for (MavenIndex index : myLocalIndexes) {
      if (index.hasGroupId(groupId)) return true;
    }
    return false;
  }

  public boolean hasLocalArtifactId(String groupId, String artifactId) {
    for (MavenIndex index : myLocalIndexes) {
      if (index.hasArtifactId(groupId, artifactId)) return true;
    }
    return false;
  }

  public boolean hasLocalVersion(String groupId, String artifactId, String version) {
    for (MavenIndex index : myLocalIndexes) {
      if (index.hasVersion(groupId, artifactId, version)) return true;
    }
    return false;
  }
}
