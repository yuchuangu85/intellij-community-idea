// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.quickfix.createFromUsage.createVariable

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.core.quickfix.QuickFixUtil
import org.jetbrains.kotlin.idea.intentions.ConvertToBlockBodyIntention
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.CreateFromUsageFixBase
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.callableBuilder.*
import org.jetbrains.kotlin.idea.util.application.executeCommand
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getAssignmentByLHS
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypeAndBranch
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElement
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.types.Variance
import java.util.*

object CreateLocalVariableActionFactory : KotlinSingleIntentionActionFactory() {
    override fun createAction(diagnostic: Diagnostic): IntentionAction? {
        val refExpr = QuickFixUtil.getParentElementOfType(diagnostic, KtNameReferenceExpression::class.java) ?: return null
        if (refExpr.getQualifiedElement() != refExpr) return null
        if (refExpr.getParentOfTypeAndBranch<KtCallableReferenceExpression> { callableReference } != null) return null

        if (getContainer(refExpr) == null) return null
        return CreateLocalFromUsageAction(refExpr)
    }

    private fun getContainer(refExpr: KtNameReferenceExpression) = refExpr.parents
        .filter { it is KtBlockExpression || it is KtDeclarationWithBody && it.bodyExpression != null }
        .firstOrNull() as? KtElement

    class CreateLocalFromUsageAction(refExpr: KtNameReferenceExpression, val propertyName: String = refExpr.getReferencedName())
        : CreateFromUsageFixBase<KtNameReferenceExpression>(refExpr) {
        override fun getText(): String = KotlinBundle.message("fix.create.from.usage.local.variable", propertyName)

        override fun invoke(project: Project, editor: Editor?, file: KtFile) {
            val refExpr = element ?: return
            val container = getContainer(refExpr) ?: return
            val assignment = refExpr.getAssignmentByLHS()
            val varExpected = assignment != null
            var originalElement: KtExpression = assignment ?: refExpr

            val actualContainer = when (container) {
                is KtBlockExpression -> container
                else -> ConvertToBlockBodyIntention.convert(container as KtDeclarationWithBody, true).bodyExpression!!
            } as KtBlockExpression

            if (actualContainer != container) {
                val bodyExpression = actualContainer.statements.first()!!
                originalElement = (bodyExpression as? KtReturnExpression)?.returnedExpression ?: bodyExpression
            }

            val typeInfo = TypeInfo(
                originalElement.getExpressionForTypeGuess(),
                if (varExpected) Variance.INVARIANT else Variance.OUT_VARIANCE
            )
            val propertyInfo =
                PropertyInfo(propertyName, TypeInfo.Empty, typeInfo, varExpected, Collections.singletonList(actualContainer))

            with(CallableBuilderConfiguration(listOfNotNull(propertyInfo), originalElement, file, editor).createBuilder()) {
                placement = CallablePlacement.NoReceiver(actualContainer)
                project.executeCommand(text) { build() }
            }
        }
    }
}
