// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.framework

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.DummyLibraryProperties
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryKind
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration
import com.intellij.openapi.roots.ui.configuration.libraries.CustomLibraryDescription
import com.intellij.openapi.roots.ui.configuration.libraries.LibraryPresentationManager
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.Nls
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.VersionView
import org.jetbrains.kotlin.config.apiVersionView
import org.jetbrains.kotlin.config.languageVersionView
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCommonCompilerArgumentsHolder
import org.jetbrains.kotlin.idea.configuration.KotlinWithLibraryConfigurator
import org.jetbrains.kotlin.idea.configuration.KotlinWithLibraryConfigurator.FileState.COPY
import org.jetbrains.kotlin.idea.configuration.KotlinWithLibraryConfigurator.FileState.DO_NOT_COPY
import org.jetbrains.kotlin.idea.configuration.NotificationMessageCollector
import org.jetbrains.kotlin.idea.configuration.createConfigureKotlinNotificationCollector
import org.jetbrains.kotlin.idea.configuration.getConfiguratorByName
import org.jetbrains.kotlin.idea.framework.ui.CreateLibraryDialog
import org.jetbrains.kotlin.idea.framework.ui.FileUIUtils
import org.jetbrains.kotlin.idea.util.projectStructure.findLibrary
import org.jetbrains.kotlin.idea.util.projectStructure.getModuleDir
import org.jetbrains.kotlin.idea.util.projectStructure.replaceFileRoot
import java.io.File
import java.nio.file.Path
import javax.swing.JComponent
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists

/**
 * @param project null when project doesn't exist yet (called from project wizard)
 */
abstract class CustomLibraryDescriptorWithDeferredConfig(
    val project: Project?,
    private val configuratorName: String,
    private val libraryName: String,
    @Nls private val dialogTitle: String,
    @org.jetbrains.annotations.Nls private val modulesSeparatorCaption: String,
    private val libraryKind: LibraryKind,
    private val suitableLibraryKinds: Set<LibraryKind>
) : CustomLibraryDescription() {
    private val projectBaseDir: VirtualFile? = project?.baseDir

    var copyFileRequests: DeferredCopyFileRequests? = null
        private set

    override fun getSuitableLibraryKinds(): Set<LibraryKind> {
        return suitableLibraryKinds
    }

    fun finishLibConfiguration(
        module: Module,
        rootModel: ModifiableRootModel,
        isNewProject: Boolean
    ) {
        configureKotlinSettings(module.project, rootModel.sdk)

        if (isNewProject) {
            KotlinCommonCompilerArgumentsHolder.getInstance(module.project).update {
                languageVersionView = VersionView.Specific(LanguageVersion.LATEST_STABLE)
                apiVersionView = VersionView.Specific(LanguageVersion.LATEST_STABLE)
            }
        }

        val library = rootModel.orderEntries().findLibrary { library ->
            val libraryPresentationManager = LibraryPresentationManager.getInstance()
            val classFiles = library.getFiles(OrderRootType.CLASSES).toList()

            libraryPresentationManager.isLibraryOfKind(classFiles, libraryKind)
        } ?: return

        val model = library.modifiableModel
        try {
            val collector = createConfigureKotlinNotificationCollector(module.project)
            copyFileRequests?.performRequests(module.getModuleDir(), model, collector)

            val copyToPath = KotlinWithLibraryConfigurator.getPathFromLibraryUrls(model.getUrls(OrderRootType.CLASSES))
            if (copyToPath != null) {
                val jarState = if (copyFileRequests?.isEmpty() != false) DO_NOT_COPY else COPY

                // Now that we know the SDK which is going to be set for the module, we can add jre 7/8 if required
                val descriptorsWithSdk = configurator.getLibraryJarDescriptors(rootModel.sdk)
                for (jarDescriptor in descriptorsWithSdk) {
                    if (model.getFiles(jarDescriptor.orderRootType).any { it.name == jarDescriptor.jarName })
                        continue

                    configurator.configureLibraryJar(model, jarState, copyToPath, jarDescriptor, collector)
                }
            }

            collector.showNotification()
        } finally {
            model.commit()
        }
    }

    protected open fun configureKotlinSettings(project: Project, sdk: Sdk?) {
    }

    class DeferredCopyFileRequests(private val configurator: KotlinWithLibraryConfigurator) {
        private val copyFilesRequests = arrayListOf<CopyFileRequest>()

        fun isEmpty() = copyFilesRequests.isEmpty()

        fun performRequests(relativePath: String, model: Library.ModifiableModel, collector: NotificationMessageCollector) {
            for (request in copyFilesRequests) {
                val destinationPath = if (FileUtil.isAbsolute(request.toDir))
                    request.toDir
                else
                    File(relativePath, request.toDir).path

                val resultFile = configurator.copyFileToDir(request.path, destinationPath, collector)

                if (request.replaceInLib && resultFile != null) {
                    model.replaceFileRoot(request.path.toFile(), resultFile.toFile())
                }
            }
        }

        fun addCopyWithReplaceRequest(path: Path, copyIntoPath: String) {
            copyFilesRequests.add(CopyFileRequest(copyIntoPath, path, true))
        }

        class CopyFileRequest(val toDir: String, val path: Path, val replaceInLib: Boolean)
    }

    override fun createNewLibrary(parentComponent: JComponent, contextDirectory: VirtualFile?): NewLibraryConfiguration? {
        val configurator = configurator

        copyFileRequests = DeferredCopyFileRequests(configurator)

        val defaultPathToJarFile = if (projectBaseDir == null)
            DEFAULT_LIB_DIR_NAME
        else
            FileUIUtils.createRelativePath(null, projectBaseDir, DEFAULT_LIB_DIR_NAME)

        val jarDescriptors = configurator.getLibraryJarDescriptors(null)

        val stdJarInDefaultPath = Path(defaultPathToJarFile, jarDescriptors.first().jarName)
        val libraryFiles = mutableListOf<Path>()
        val librarySourceFiles = mutableListOf<Path>()
        if (projectBaseDir != null && stdJarInDefaultPath.exists()) {
            libraryFiles.add(stdJarInDefaultPath)
            for (jarDescriptor in jarDescriptors) {
                var destination = Path(defaultPathToJarFile, jarDescriptor.jarName)
                if (destination.notExists()) {
                    copyFileRequests!!.addCopyWithReplaceRequest(jarDescriptor.getPathInPlugin(), defaultPathToJarFile)
                    destination = jarDescriptor.getPathInPlugin()
                }

                if (jarDescriptor.orderRootType == OrderRootType.SOURCES) {
                    librarySourceFiles.add(destination)
                } else {
                    libraryFiles.add(destination)
                }
            }
        } else {
            val dialog = CreateLibraryDialog(defaultPathToJarFile, dialogTitle, modulesSeparatorCaption)
            dialog.show()

            if (!dialog.isOK) return null

            val copyIntoPath = dialog.copyIntoPath
            if (copyIntoPath != null) {
                for (libraryJarDescriptor in configurator.getLibraryJarDescriptors(null)) {
                    copyFileRequests!!.addCopyWithReplaceRequest(libraryJarDescriptor.getPathInPlugin(), copyIntoPath)
                }
            }

            for (jarDescriptor in jarDescriptors) {
                if (jarDescriptor.orderRootType == OrderRootType.SOURCES) {
                    librarySourceFiles.add(jarDescriptor.getPathInPlugin())
                } else {
                    libraryFiles.add(jarDescriptor.getPathInPlugin())
                }
            }
        }

        return createConfiguration(libraryFiles, librarySourceFiles)
    }

    private val configurator: KotlinWithLibraryConfigurator
        get() = getConfiguratorByName(configuratorName) as KotlinWithLibraryConfigurator?
            ?: error("Configurator with name $configuratorName should exists")

    // Implements an API added in IDEA 16
    override fun createNewLibraryWithDefaultSettings(contextDirectory: VirtualFile?): NewLibraryConfiguration? {
        return createConfigurationFromPluginPaths()
    }

    fun createConfigurationFromPluginPaths() = createConfiguration(
        collectPathsInPlugin(OrderRootType.CLASSES),
        collectPathsInPlugin(OrderRootType.SOURCES)
    )

    private fun collectPathsInPlugin(rootType: OrderRootType): List<Path> = configurator.getLibraryJarDescriptors(null)
        .filter { it.orderRootType == rootType }
        .map { it.getPathInPlugin() }

    protected fun createConfiguration(libraryFiles: List<Path>, librarySourceFiles: List<Path>): NewLibraryConfiguration =
        object : NewLibraryConfiguration(libraryName, configurator.libraryType, DummyLibraryProperties.INSTANCE) {
            override fun addRoots(editor: LibraryEditor) {
                for (libraryFile in libraryFiles) {
                    editor.addRoot(VfsUtil.getUrlForLibraryRoot(libraryFile.toFile()), OrderRootType.CLASSES)
                }

                for (librarySrcFile in librarySourceFiles) {
                    editor.addRoot(VfsUtil.getUrlForLibraryRoot(librarySrcFile.toFile()), OrderRootType.SOURCES)
                }
            }
        }

    companion object {

        private const val DEFAULT_LIB_DIR_NAME = "lib"
    }
}
