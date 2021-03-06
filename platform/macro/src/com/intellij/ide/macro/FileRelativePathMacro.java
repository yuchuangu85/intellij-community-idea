/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package com.intellij.ide.macro;

import com.intellij.ide.IdeCoreBundle;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class FileRelativePathMacro extends Macro {
  @NotNull
  @Override
  public String getName() {
    return "FileRelativePath";
  }

  @NotNull
  @Override
  public String getDescription() {
    return IdeCoreBundle.message("macro.file.path.relative");
  }

  @Override
  public String expand(@NotNull DataContext dataContext) {
    Project project = CommonDataKeys.PROJECT.getData(dataContext);
    final VirtualFile baseDir = project == null ? null : project.getBaseDir();
    if (baseDir == null) {
      return null;
    }

    VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
    if (file == null) return null;
    return FileUtil.getRelativePath(VfsUtil.virtualToIoFile(baseDir), VfsUtil.virtualToIoFile(file));
  }
}
