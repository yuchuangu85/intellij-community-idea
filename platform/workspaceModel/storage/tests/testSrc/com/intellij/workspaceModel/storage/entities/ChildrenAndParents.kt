// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.workspaceModel.storage

import com.intellij.workspaceModel.storage.impl.EntityDataDelegation
import com.intellij.workspaceModel.storage.impl.ModifiableWorkspaceEntityBase
import com.intellij.workspaceModel.storage.impl.WorkspaceEntityBase
import com.intellij.workspaceModel.storage.impl.WorkspaceEntityData
import com.intellij.workspaceModel.storage.impl.references.ManyToOne
import com.intellij.workspaceModel.storage.impl.references.MutableManyToOne
import com.intellij.workspaceModel.storage.impl.references.MutableOneToMany
import com.intellij.workspaceModel.storage.impl.references.OneToMany

// ------------------- Parent Entity --------------------------------

class ParentEntityData : WorkspaceEntityData<ParentEntity>() {

  lateinit var parentProperty: String

  override fun createEntity(snapshot: WorkspaceEntityStorage): ParentEntity {
    return ParentEntity(parentProperty).also { addMetaData(it, snapshot) }
  }
}

class ParentEntity(
  val parentProperty: String
) : WorkspaceEntityBase() {

  val children: Sequence<ChildEntity> by OneToMany(ChildEntity::class.java, false)

  val optionalChildren: Sequence<ChildWithOptionalParentEntity> by OneToMany(ChildWithOptionalParentEntity::class.java, true)
}

class ModifiableParentEntity : ModifiableWorkspaceEntityBase<ParentEntity>() {
  var parentProperty: String by EntityDataDelegation()
  var children: Sequence<ChildEntity> by MutableOneToMany(ParentEntity::class.java, ChildEntity::class.java, false)
  var optionalChildren: Sequence<ChildWithOptionalParentEntity> by MutableOneToMany(
    ParentEntity::class.java, ChildWithOptionalParentEntity::class.java, true)
}

fun WorkspaceEntityStorageBuilder.addParentEntity(parentProperty: String = "parent", source: EntitySource = MySource) =
  addEntity(ModifiableParentEntity::class.java, source) {
    this.parentProperty = parentProperty
  }

// ---------------- Child entity ----------------------

data class DataClass(val stringProperty: String, val parent: EntityReference<ParentEntity>)

class ChildEntityData : WorkspaceEntityData<ChildEntity>() {
  lateinit var childProperty: String
  var dataClass: DataClass? = null
  override fun createEntity(snapshot: WorkspaceEntityStorage): ChildEntity {
    return ChildEntity(childProperty, dataClass).also { addMetaData(it, snapshot) }
  }
}

class ChildEntity(
  val childProperty: String,
  val dataClass: DataClass?
) : WorkspaceEntityBase() {
  val parent: ParentEntity by ManyToOne.NotNull(ParentEntity::class.java)
}

class ModifiableChildEntity : ModifiableWorkspaceEntityBase<ChildEntity>() {
  var childProperty: String by EntityDataDelegation()
  var dataClass: DataClass? by EntityDataDelegation()
  var parent: ParentEntity by MutableManyToOne.NotNull(ChildEntity::class.java, ParentEntity::class.java)
}


fun WorkspaceEntityStorageBuilder.addChildEntity(parentEntity: ParentEntity,
                                                 childProperty: String = "child",
                                                 dataClass: DataClass? = null,
                                                 source: EntitySource = MySource) =
  addEntity(ModifiableChildEntity::class.java, source) {
    this.parent = parentEntity
    this.childProperty = childProperty
    this.dataClass = dataClass
  }

// -------------------- Child with optional parent ---------------

class ChildWithOptionalParentEntityData : WorkspaceEntityData<ChildWithOptionalParentEntity>() {
  lateinit var childProperty: String
  override fun createEntity(snapshot: WorkspaceEntityStorage): ChildWithOptionalParentEntity {
    return ChildWithOptionalParentEntity(childProperty).also { addMetaData(it, snapshot) }
  }
}

class ChildWithOptionalParentEntity(val childProperty: String) : WorkspaceEntityBase() {
  val optionalParent: ParentEntity? by ManyToOne.Nullable(ParentEntity::class.java)
}

class ModifiableChildWithOptionalParentEntity : ModifiableWorkspaceEntityBase<ChildWithOptionalParentEntity>() {
  var optionalParent: ParentEntity? by MutableManyToOne.Nullable(ChildWithOptionalParentEntity::class.java, ParentEntity::class.java)
  var childProperty: String by EntityDataDelegation()
}


fun WorkspaceEntityStorageBuilder.addChildWithOptionalParentEntity(parentEntity: ParentEntity?,
                                                                   childProperty: String = "child",
                                                                   source: EntitySource = MySource) =
  addEntity(ModifiableChildWithOptionalParentEntity::class.java, source) {
    this.optionalParent = parentEntity
    this.childProperty = childProperty
  }

// --------------------- Child with two parents ----------------------

class ChildChildEntityData : WorkspaceEntityData<ChildChildEntity>() {
  override fun createEntity(snapshot: WorkspaceEntityStorage): ChildChildEntity {
    return ChildChildEntity().also { addMetaData(it, snapshot) }
  }
}

class ChildChildEntity : WorkspaceEntityBase() {
  val parent1: ParentEntity by ManyToOne.NotNull(ParentEntity::class.java)
  val parent2: ChildEntity by ManyToOne.NotNull(ChildEntity::class.java)
}

class ModifiableChildChildEntity : ModifiableWorkspaceEntityBase<ChildChildEntity>() {
  var parent1: ParentEntity by MutableManyToOne.NotNull(ChildChildEntity::class.java, ParentEntity::class.java)
  var parent2: ChildEntity by MutableManyToOne.NotNull(ChildChildEntity::class.java, ChildEntity::class.java)
}

fun WorkspaceEntityStorageBuilder.addChildChildEntity(parent1: ParentEntity, parent2: ChildEntity) =
  addEntity(ModifiableChildChildEntity::class.java, MySource) {
    this.parent1 = parent1
    this.parent2 = parent2
  }

