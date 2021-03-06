// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInspection.*
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.core.implicitModality
import org.jetbrains.kotlin.idea.quickfix.RemoveModifierFix
import org.jetbrains.kotlin.psi.declarationVisitor
import org.jetbrains.kotlin.psi.psiUtil.modalityModifier

class RedundantModalityModifierInspection : AbstractKotlinInspection(), CleanupLocalInspectionTool {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return declarationVisitor { declaration ->
            val modalityModifier = declaration.modalityModifier() ?: return@declarationVisitor
            val modalityModifierType = modalityModifier.node.elementType
            val implicitModality = declaration.implicitModality()

            if (modalityModifierType != implicitModality) return@declarationVisitor

            holder.registerProblem(
                modalityModifier,
                KotlinBundle.message("redundant.modality.modifier"),
                ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                IntentionWrapper(RemoveModifierFix(declaration, implicitModality, isRedundant = true))
            )
        }
    }
}
