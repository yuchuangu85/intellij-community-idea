// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.jps.maven.compiler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.artifacts.ArtifactBuildTaskProvider;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.BuildTask;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.FSOperations;
import org.jetbrains.jps.incremental.artifacts.impl.JpsArtifactUtil;
import org.jetbrains.jps.incremental.fs.CompilationRound;
import org.jetbrains.jps.maven.model.JpsMavenExtensionService;
import org.jetbrains.jps.maven.model.impl.MavenModuleResourceConfiguration;
import org.jetbrains.jps.maven.model.impl.MavenProjectConfiguration;
import org.jetbrains.jps.model.artifact.JpsArtifact;
import org.jetbrains.jps.model.artifact.elements.JpsArtifactRootElement;
import org.jetbrains.jps.model.artifact.elements.JpsDirectoryPackagingElement;
import org.jetbrains.jps.model.artifact.elements.JpsFileCopyPackagingElement;
import org.jetbrains.jps.model.ex.JpsElementBase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class MavenManifestGenerationBuildTaskProvider extends ArtifactBuildTaskProvider {
  @NotNull
  @Override
  public List<? extends BuildTask> createArtifactBuildTasks(@NotNull JpsArtifact artifact,
                                                            @NotNull ArtifactBuildPhase buildPhase) {
    String artifactName = artifact.getName();
    if (buildPhase == ArtifactBuildPhase.PRE_PROCESSING && (artifactName.endsWith(" exploded") || artifactName.endsWith("ejb-client"))
        && artifact.getRootElement() instanceof JpsArtifactRootElement) {
      return Collections.singletonList(new MavenManifestGenerationBuildTask(artifact));
    }
    return Collections.emptyList();
  }

  private static class MavenManifestGenerationBuildTask extends BuildTask {
    private static final Logger LOG = Logger.getInstance(MavenManifestGenerationBuildTask.class);
    private final JpsArtifact myArtifact;

    MavenManifestGenerationBuildTask(JpsArtifact artifact) {
      myArtifact = artifact;
    }

    @Override
    public void build(CompileContext context) {
      BuildDataPaths dataPaths = context.getProjectDescriptor().dataManager.getDataPaths();
      MavenProjectConfiguration projectConfiguration = JpsMavenExtensionService.getInstance().getMavenProjectConfiguration(dataPaths);
      if (projectConfiguration == null) return;

      final MavenModuleResourceConfiguration moduleResourceConfiguration = projectConfiguration.moduleConfigurations.get(getModuleName(myArtifact.getName()));
      if (moduleResourceConfiguration != null && StringUtil.isNotEmpty(moduleResourceConfiguration.manifest)) {
        try {
          File output = new File(myArtifact.getOutputPath(), JarFile.MANIFEST_NAME);
          FileUtil.writeToFile(output, Base64.getDecoder().decode(moduleResourceConfiguration.manifest));
          handleSkinnyWars(context, projectConfiguration, moduleResourceConfiguration);
        }
        // do not fail the whole 'Make' if there is an invalid manifest cached (e.g. non encoded string generated by previous IDEA version)
        catch (Exception e) {
          LOG.debug(e);
        }
      }
    }

    private void handleSkinnyWars(final CompileContext context,
                                  final MavenProjectConfiguration projectConfiguration,
                                  MavenModuleResourceConfiguration moduleResourceConfiguration) {
      if (!"ear".equals(moduleResourceConfiguration.modelMap.get("packaging"))) return;
      if (!Boolean.parseBoolean(moduleResourceConfiguration.modelMap.get("build.plugin.maven-ear-plugin.skinnyWars"))) return;

      final String earClasspath = moduleResourceConfiguration.classpath;
      if (earClasspath == null) return;

      final Map<String, String> earClasspathMap = ContainerUtil.map2Map(
        StringUtil.split(earClasspath, " "), s -> {
          final int idx = s.lastIndexOf("/");
          return Pair.create(s.substring(idx == -1 ? 0 : idx + 1), s);
        });

      JpsArtifactUtil.processPackagingElements(myArtifact.getRootElement(), element -> {
        if (!(element instanceof JpsFileCopyPackagingElement)) return true;
        final JpsFileCopyPackagingElement fileCopyPackagingElement = (JpsFileCopyPackagingElement)element;

        final String filePath = fileCopyPackagingElement.getFilePath();
        final File skinnyManifest = new File(filePath);
        if (!"SKINNY_MANIFEST.MF".equals(skinnyManifest.getName())) return true;

        final String skinnyWarModuleName = skinnyManifest.getParentFile().getParentFile().getName();
        final MavenModuleResourceConfiguration warConfiguration = projectConfiguration.moduleConfigurations.get(skinnyWarModuleName);
        if (warConfiguration == null || warConfiguration.classpath == null) return true;

        try {
          final byte[] warManifestData = Base64.getDecoder().decode(warConfiguration.manifest);
          Manifest warManifest = new Manifest(new ByteArrayInputStream(warManifestData));

          List<String> skinnyWarClasspath = new ArrayList<>();
          for (String entry : StringUtil.split(warConfiguration.classpath, " ")) {
            final int idx = entry.lastIndexOf("/");
            final String entryName = entry.substring(idx == -1 ? 0 : idx + 1);
            final String earEntryPath = earClasspathMap.get(entryName);
            skinnyWarClasspath.add(earEntryPath == null ? entry : earEntryPath);
          }

          final Attributes warManifestMainAttributes = warManifest.getMainAttributes();
          warManifestMainAttributes.putValue("Class-Path", StringUtil.join(skinnyWarClasspath, " "));

          File skinnyManifestTargetFile = null;
          FileUtil.createParentDirs(skinnyManifest);
          try (FileOutputStream outputStream = new FileOutputStream(skinnyManifest)) {
            warManifest.write(outputStream);

            if (fileCopyPackagingElement instanceof JpsElementBase) {
              final LinkedList<String> pathParts = new LinkedList<>();
              pathParts.add(fileCopyPackagingElement.getRenamedOutputFileName());

              JpsElementBase parent = ((JpsElementBase<?>)fileCopyPackagingElement).getParent();
              while (parent != null) {
                if (parent instanceof JpsDirectoryPackagingElement) {
                  pathParts.addFirst(((JpsDirectoryPackagingElement)parent).getDirectoryName());
                }
                else if (parent instanceof JpsArtifact) {
                  final String outputPath = ((JpsArtifact)parent).getOutputPath();
                  if (outputPath != null) {
                    pathParts.addFirst(outputPath);
                    skinnyManifestTargetFile = new File(StringUtil.join(pathParts, "/"));
                    break;
                  }
                }
                parent = parent.getParent();
              }
            }
          }

          if (skinnyManifestTargetFile != null) {
            FileUtil.createParentDirs(skinnyManifestTargetFile);
            FileUtil.copy(skinnyManifest, skinnyManifestTargetFile);
          }

          FSOperations.markDirtyIfNotDeleted(context, CompilationRound.NEXT, skinnyManifest);
          FSOperations.markDirtyIfNotDeleted(context, CompilationRound.NEXT, skinnyManifestTargetFile);
        }
        catch (IOException e) {
          LOG.debug(e);
        }

        return true;
      });
    }

    @Nullable
    private static String getModuleName(@NotNull String artifactName) {
      return StringUtil.substringBefore(artifactName, ":");
    }
  }
}