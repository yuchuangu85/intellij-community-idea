/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.idea.maven.indices;

import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.server.MavenIndexerWrapper;
import org.jetbrains.idea.maven.utils.MavenLog;
import org.jetbrains.idea.maven.utils.MavenProcessCanceledException;
import org.jetbrains.idea.maven.utils.MavenProgressIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MavenIndices {
  private final MavenIndexerWrapper myIndexer;

  private final File myIndicesDir;
  private final MavenSearchIndex.IndexListener myListener;

  private final List<MavenIndex> myIndices = new CopyOnWriteArrayList<>();
  private static final Object ourDirectoryLock = new Object();

  public MavenIndices(MavenIndexerWrapper indexer, File indicesDir, MavenSearchIndex.IndexListener listener) {
    myIndexer = indexer;
    myIndicesDir = indicesDir;
    myListener = listener;

    load();
  }

  private void load() {
    File[] indices = myIndicesDir.listFiles();
    if (indices == null) return;
    Arrays.sort(indices);

    for (File each : indices) {
      if (!each.isDirectory()) continue;

      try {
        MavenIndex index = new MavenIndex(myIndexer, each, myListener);
        if (find(index.getRepositoryPathOrUrl(), index.getKind()) != null) {
          index.close(true);
          FileUtil.delete(each);
          continue;
        }
        myIndices.add(index);
      }
      catch (Exception e) {
        FileUtil.delete(each);
        MavenLog.LOG.warn(e);
      }
    }
  }

  public void close() {
    for (MavenSearchIndex each : myIndices) {
      each.close(false);
    }
    myIndices.clear();
  }

  public List<MavenIndex> getIndices() {
    return new ArrayList<>(myIndices);
  }

  public MavenIndex add(String repositoryId, String repositoryPathOrUrl, MavenSearchIndex.Kind kind) throws MavenIndexException {
    MavenIndex index = find(repositoryPathOrUrl, kind);
    if (index != null) {
      index.registerId(repositoryId);
      return index;
    }

    File dir = createNewIndexDir();
    index = new MavenIndex(myIndexer, dir, repositoryId, repositoryPathOrUrl, kind, myListener);
    myIndices.add(index);
    return index;
  }

  @Nullable
  public MavenIndex find(String repositoryPathOrUrl, MavenSearchIndex.Kind kind) {
    for (MavenIndex each : myIndices) {
      if (each.isFor(kind, repositoryPathOrUrl)) return each;
    }
    return null;
  }

  private File createNewIndexDir() {
    return createNewDir(myIndicesDir, "Index", 1000);
  }

  @NotNull
  static File createNewDir(File parent, String prefix, int max) {
    synchronized (ourDirectoryLock) {
      for (int i = 0; i < max; i++) {
        String name = prefix + i;
        File f = new File(parent, name);
        if (!f.exists()) {
          boolean createSuccessFull = f.mkdirs();
          if (createSuccessFull) {
            return f;
          }
        }
      }
      throw new RuntimeException("No available dir found");
    }
  }

  public static void updateOrRepair(MavenSearchIndex index, boolean fullUpdate, @Nullable MavenGeneralSettings settings, MavenProgressIndicator progress)
    throws MavenProcessCanceledException {
    index.updateOrRepair(fullUpdate, settings, progress);
  }
}