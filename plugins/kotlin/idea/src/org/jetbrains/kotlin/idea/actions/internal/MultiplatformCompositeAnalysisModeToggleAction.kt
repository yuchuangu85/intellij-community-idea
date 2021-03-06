// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.actions.internal

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.project.KotlinMultiplatformAnalysisModeComponent
import org.jetbrains.kotlin.idea.project.KotlinMultiplatformAnalysisModeComponent.Mode
import org.jetbrains.kotlin.idea.project.useCompositeAnalysis

class MultiplatformCompositeAnalysisToggleAction :
    ToggleAction(
        KotlinBundle.message("title.toggle.composite.analysis.mode.for.multiplatform"),
        KotlinBundle.message("analyse.modules.with.different.platforms.together"),
        null
    ) {

    override fun isSelected(e: AnActionEvent): Boolean = e.project?.useCompositeAnalysis == true

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        KotlinMultiplatformAnalysisModeComponent.setMode(
            e.project ?: return,
            if (state) Mode.COMPOSITE else Mode.SEPARATE
        )
    }
}