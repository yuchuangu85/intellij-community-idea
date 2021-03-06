// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.tools.projectWizard.wizard

import com.intellij.ide.IdeBundle
import com.intellij.openapi.application.JBProtocolCommand
import org.jetbrains.kotlin.tools.projectWizard.projectTemplates.ProjectTemplate
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class OpenNewProjectWizardProtocolCommand : JBProtocolCommand(COMMAND_NAME) {
    override fun perform(target: String?, parameters: MutableMap<String, String>, fragment: String?): Future<String?> =
        if (target != NEW_PROJECT_TARGET) CompletableFuture.completedFuture(IdeBundle.message("jb.protocol.unknown.target", target))
        else {
            showCreateNewProjectWizard(parameters)
            CompletableFuture.completedFuture(null)
        }

    private fun showCreateNewProjectWizard(parameters: Map<String, String?>) {
        val template = parameters[NEW_PROJECT_TARGET_TEMPLATE_PARAMETER]
            ?.let(ProjectTemplate.Companion::byId)

        NewWizardOpener.open(template)
    }

    companion object {
        private const val COMMAND_NAME = "kotlin-wizard"
        private const val NEW_PROJECT_TARGET = "create-project"
        private const val NEW_PROJECT_TARGET_TEMPLATE_PARAMETER = "template"
    }
}
