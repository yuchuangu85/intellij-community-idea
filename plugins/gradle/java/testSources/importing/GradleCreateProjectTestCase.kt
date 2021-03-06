// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.gradle.importing

import com.intellij.ide.impl.NewProjectUtil
import com.intellij.ide.projectWizard.NewProjectWizard
import com.intellij.ide.projectWizard.ProjectTypeStep
import com.intellij.ide.util.newProjectWizard.AbstractProjectWizard
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.service.remote.ExternalSystemProgressNotificationManagerImpl
import com.intellij.openapi.externalSystem.test.ExternalSystemImportingTestCase
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil.findProjectData
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil.getSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.roots.ui.configuration.DefaultModulesProvider
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.roots.ui.configuration.actions.NewModuleAction
import com.intellij.openapi.util.text.StringUtil.convertLineSeparators
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.RunAll
import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import org.gradle.util.GradleVersion
import org.jetbrains.plugins.gradle.org.jetbrains.plugins.gradle.util.ProjectInfoBuilder
import org.jetbrains.plugins.gradle.org.jetbrains.plugins.gradle.util.ProjectInfoBuilder.ModuleInfo
import org.jetbrains.plugins.gradle.org.jetbrains.plugins.gradle.util.ProjectInfoBuilder.ProjectInfo
import org.jetbrains.plugins.gradle.service.project.wizard.GradleFrameworksWizardStep
import org.jetbrains.plugins.gradle.service.project.wizard.GradleStructureWizardStep
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.util.GradleConstants
import org.jetbrains.plugins.gradle.util.isSupported
import org.jetbrains.plugins.gradle.util.runReadActionAndWait
import org.jetbrains.plugins.gradle.util.waitForProjectReload
import java.io.File
import com.intellij.openapi.externalSystem.util.use as utilUse


abstract class GradleCreateProjectTestCase : UsefulTestCase() {

  private val bareFixture = IdeaTestFixtureFactory.getFixtureFactory().createBareFixture()
  private val tempDirFixture = IdeaTestFixtureFactory.getFixtureFactory().createTempDirTestFixture()
  private val sdkFixture = IdeaTestFixtureFactory.getFixtureFactory().createSdkFixture()

  override fun setUp() {
    super.setUp()
    tempDirFixture.setUp()
    bareFixture.setUp()
    sdkFixture.setUp()
    sdkFixture.setUpSdk(JavaSdk.getInstance()) {
      isSupported(GradleVersion.current(), it)
    }
  }

  override fun tearDown() {
    RunAll(
      { ExternalSystemProgressNotificationManagerImpl.assertListenersReleased() },
      { ExternalSystemProgressNotificationManagerImpl.cleanupListeners() },
      { sdkFixture.tearDown() },
      { bareFixture.tearDown() },
      { tempDirFixture.tearDown() },
      { super.tearDown() }
    ).run()
  }

  fun Project.assertProjectStructure(projectInfo: ProjectInfo) {
    ExternalSystemImportingTestCase.assertModules(
      this,
      projectInfo.rootModule.ideName,
      *projectInfo.rootModule.modulesPerSourceSet.toTypedArray(),
      *projectInfo.modules.map { it.ideName }.toTypedArray(),
      *projectInfo.modules.flatMap { it.modulesPerSourceSet }.toTypedArray())
  }

  fun deleteProject(projectInfo: ProjectInfo) {
    ApplicationManager.getApplication().invokeAndWait {
      runWriteAction {
        for (module in projectInfo.modules) {
          val root = module.root
          if (root.exists()) {
            root.delete(null)
          }
        }
      }
    }
  }

  fun projectInfo(id: String, useKotlinDsl: Boolean = false, configure: ProjectInfoBuilder.() -> Unit): ProjectInfo {
    val tempDirectory = runReadActionAndWait {
      LocalFileSystem.getInstance()
        .findFileByPath(tempDirFixture.tempDirPath)!!
    }
    return ProjectInfoBuilder.projectInfo(id, tempDirectory) {
      this.useKotlinDsl = useKotlinDsl
      configure()
    }
  }

  protected fun Project.assertDefaultProjectSettings() {
    val externalProjectPath = basePath!!
    val settings = getSettings(this, GradleConstants.SYSTEM_ID) as GradleSettings
    val projectSettings = settings.getLinkedProjectSettings(externalProjectPath)!!
    assertEquals(projectSettings.externalProjectPath, externalProjectPath)
    assertEquals(projectSettings.isUseQualifiedModuleNames, true)
    assertEquals(settings.storeProjectFilesExternally, true)
  }

  fun withProject(projectInfo: ProjectInfo, save: Boolean = false, action: Project.() -> Unit) {
    createProject(projectInfo).use(save = save) { project ->
      for (moduleInfo in projectInfo.modules) {
        createModule(moduleInfo, project)
      }
      project.action()
    }
  }

  private fun createProject(projectInfo: ProjectInfo): Project {
    return createProject(projectInfo.rootModule.root.path) { step ->
      configureWizardStepSettings(step, projectInfo.rootModule, null)
    }
  }

  private fun createModule(moduleInfo: ModuleInfo, project: Project) {
    val parentData = findProjectData(project, GradleConstants.SYSTEM_ID, project.basePath!!)!!
    return createModule(moduleInfo.root.path, project) { step ->
      configureWizardStepSettings(step, moduleInfo, parentData.data)
    }
  }

  private fun configureWizardStepSettings(step: ModuleWizardStep, moduleInfo: ModuleInfo, parentData: ProjectData?) {
    when (step) {
      is ProjectTypeStep -> {
        step.setSelectedTemplate("Gradle", null)
        val frameworksStep = step.frameworksStep
        frameworksStep as GradleFrameworksWizardStep
        frameworksStep.setUseKotlinDsl(moduleInfo.useKotlinDsl)
      }
      is GradleStructureWizardStep -> {
        step.parentData = parentData
        moduleInfo.groupId?.let { step.groupId = it }
        step.artifactId = moduleInfo.artifactId
        moduleInfo.version?.let { step.version = it }
        step.entityName = moduleInfo.simpleName
        step.location = moduleInfo.root.path
      }
    }
  }

  private fun createProject(directory: String, configure: (ModuleWizardStep) -> Unit): Project {
    return waitForProjectReload {
      invokeAndWaitIfNeeded {
        val wizard = createWizard(null, directory)
        wizard.runWizard(configure)
        wizard.disposeIfNeeded()
        NewProjectUtil.createFromWizard(wizard, null)
      }
    }
  }

  private fun createModule(directory: String, project: Project, configure: (ModuleWizardStep) -> Unit) {
    waitForProjectReload {
      ApplicationManager.getApplication().invokeAndWait {
        val wizard = createWizard(project, directory)
        wizard.runWizard(configure)
        wizard.disposeIfNeeded()
        NewModuleAction().createModuleFromWizard(project, null, wizard)
      }
    }
  }

  private fun createWizard(project: Project?, directory: String): AbstractProjectWizard {
    val modulesProvider = project?.let { DefaultModulesProvider(it) } ?: ModulesProvider.EMPTY_MODULES_PROVIDER
    return NewProjectWizard(project, modulesProvider, directory).also {
      PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
    }
  }

  private fun AbstractProjectWizard.runWizard(configure: ModuleWizardStep.() -> Unit) {
    while (true) {
      val currentStep = currentStepObject
      currentStep.configure()
      if (isLast) break
      doNextAction()
      if (currentStep === currentStepObject) {
        throw RuntimeException("$currentStepObject is not validated")
      }
    }
    if (!doFinishAction()) {
      throw RuntimeException("$currentStepObject is not validated")
    }
  }

  fun assertSettingsFileContent(projectInfo: ProjectInfo) {
    val builder = StringBuilder()
    val rootModuleInfo = projectInfo.rootModule
    val useKotlinDsl = rootModuleInfo.useKotlinDsl
    builder.appendln(defineProject(rootModuleInfo.artifactId, useKotlinDsl))
    for (moduleInfo in projectInfo.modules) {
      val externalName = moduleInfo.externalName
      val artifactId = moduleInfo.artifactId
      when (moduleInfo.isFlat) {
        true -> builder.appendln(includeFlatModule(externalName, useKotlinDsl))
        else -> builder.appendln(includeModule(externalName, useKotlinDsl))
      }
      if (externalName != artifactId) {
        builder.appendln(renameModule(externalName, artifactId, useKotlinDsl))
      }
    }
    val settingsFileName = getSettingsFileName(useKotlinDsl)
    val settingsFile = File(rootModuleInfo.root.path, settingsFileName)
    assertFileContent(settingsFile, builder.toString())
  }

  private fun assertFileContent(file: File, content: String) {
    val expected = convertLineSeparators(file.readText().trim())
    val actual = convertLineSeparators(content.trim())
    assertEquals(expected, actual)
  }

  fun assertBuildScriptFiles(projectInfo: ProjectInfo) {
    for (module in projectInfo.modules + projectInfo.rootModule) {
      val buildFileName = getBuildFileName(module.useKotlinDsl)
      val buildFile = File(module.root.path, buildFileName)
      assertTrue(buildFile.exists())
    }
  }

  private fun getBuildFileName(useKotlinDsl: Boolean): String {
    return when (useKotlinDsl) {
      true -> """build.gradle.kts"""
      else -> """build.gradle"""
    }
  }

  private fun getSettingsFileName(useKotlinDsl: Boolean): String {
    return when (useKotlinDsl) {
      true -> """settings.gradle.kts"""
      else -> """settings.gradle"""
    }
  }

  private fun defineProject(name: String, useKotlinDsl: Boolean): String {
    return when (useKotlinDsl) {
      true -> """rootProject.name = "$name""""
      else -> """rootProject.name = '$name'"""
    }
  }

  private fun includeModule(name: String, useKotlinDsl: Boolean): String {
    return when (useKotlinDsl) {
      true -> """include("$name")"""
      else -> """include '$name'"""
    }
  }

  private fun includeFlatModule(name: String, useKotlinDsl: Boolean): String {
    return when (useKotlinDsl) {
      true -> """includeFlat("$name")"""
      else -> """includeFlat '$name'"""
    }
  }

  private fun renameModule(from: String, to: String, useKotlinDsl: Boolean): String {
    return when (useKotlinDsl) {
      true -> """findProject(":$from")?.name = "$to""""
      else -> """findProject(':$from')?.name = '$to'"""
    }
  }

  fun Project.use(save: Boolean = false, action: (Project) -> Unit) = utilUse(save, action)
}