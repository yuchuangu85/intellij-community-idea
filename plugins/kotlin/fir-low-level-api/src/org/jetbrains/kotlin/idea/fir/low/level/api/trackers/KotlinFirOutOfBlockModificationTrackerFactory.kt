/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.fir.low.level.api.trackers

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.trackers.KotlinOutOfBlockModificationTrackerFactory

class KotlinFirOutOfBlockModificationTrackerFactory(private val project: Project) : KotlinOutOfBlockModificationTrackerFactory() {
    override fun createProjectWideOutOfBlockModificationTracker(): ModificationTracker =
        KotlinFirOutOfBlockModificationTracker(project)

    override fun createModuleWithoutDependenciesOutOfBlockModificationTracker(module: Module): ModificationTracker =
        KotlinFirOutOfBlockModuleModificationTracker(module)

    @TestOnly
    fun incrementModificationsCount() {
        project.getService(KotlinFirModificationTrackerService::class.java).increaseModificationCountForAllModules()
    }
}

private class KotlinFirOutOfBlockModificationTracker(project: Project) : ModificationTracker {
    private val trackerService = project.getService(KotlinFirModificationTrackerService::class.java)

    override fun getModificationCount(): Long =
        trackerService.projectGlobalOutOfBlockInKotlinFilesModificationCount
}

private class KotlinFirOutOfBlockModuleModificationTracker(private val module: Module) : ModificationTracker {
    private val trackerService = module.project.getService(KotlinFirModificationTrackerService::class.java)

    override fun getModificationCount(): Long =
        trackerService.getOutOfBlockModificationCountForModules(module)
}