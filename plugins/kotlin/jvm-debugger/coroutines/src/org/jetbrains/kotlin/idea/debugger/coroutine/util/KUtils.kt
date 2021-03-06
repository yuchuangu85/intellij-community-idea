// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.debugger.coroutine.util

import com.intellij.debugger.engine.JavaStackFrame
import com.intellij.debugger.jdi.StackFrameProxyImpl
import com.intellij.debugger.memory.utils.StackFrameItem
import com.sun.jdi.Location
import org.jetbrains.kotlin.idea.debugger.safeLineNumber
import org.jetbrains.kotlin.idea.debugger.safeLocation
import org.jetbrains.kotlin.idea.debugger.safeMethod

fun Location.format(): String {
    val method = safeMethod()
    return "${method?.name() ?: "noname"}:${safeLineNumber()}, ${method?.declaringType()?.name() ?: "empty"}"
}

fun JavaStackFrame.format(): String {
    val location = descriptor.location
    return location?.format() ?: "emptyLocation"
}

fun StackFrameItem.format(): String {
    val method = this.method()
    val type = this.path()
    val lineNumber = this.line()
    return "$method:$lineNumber, $type"
}

fun StackFrameProxyImpl.format(): String {
    return safeLocation()?.format() ?: "emptyLocation"
}