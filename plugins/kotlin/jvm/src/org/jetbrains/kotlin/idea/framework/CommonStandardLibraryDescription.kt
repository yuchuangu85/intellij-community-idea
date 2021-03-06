// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.framework

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.LibraryKind
import org.jetbrains.annotations.Nls
import org.jetbrains.kotlin.idea.KotlinJvmBundle

class CommonStandardLibraryDescription(project: Project?) : CustomLibraryDescriptorWithDeferredConfig(
    // TODO: KotlinCommonModuleConfigurator
    project, "common", LIBRARY_NAME, DIALOG_TITLE, LIBRARY_CAPTION, KOTLIN_COMMON_STDLIB_KIND, SUITABLE_LIBRARY_KINDS
) {
    companion object {
        val KOTLIN_COMMON_STDLIB_KIND: LibraryKind = LibraryKind.create("kotlin-stdlib-common")
        const val LIBRARY_NAME = "KotlinStdlibCommon"

        val DIALOG_TITLE
            @Nls
            get() = KotlinJvmBundle.message("create.kotlin.common.standard.library")
        val LIBRARY_CAPTION
            @Nls
            get() = KotlinJvmBundle.message("kotlin.common.standard.library")
        val SUITABLE_LIBRARY_KINDS: Set<LibraryKind> = setOf(KOTLIN_COMMON_STDLIB_KIND)
    }
}
