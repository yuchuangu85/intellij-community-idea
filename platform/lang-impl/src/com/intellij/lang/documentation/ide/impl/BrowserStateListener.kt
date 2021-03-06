// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.lang.documentation.ide.impl

import com.intellij.lang.documentation.DocumentationData
import com.intellij.lang.documentation.impl.DocumentationRequest
import com.intellij.util.concurrency.annotations.RequiresEdt
import kotlinx.coroutines.Deferred

internal fun interface BrowserStateListener {

  @RequiresEdt
  fun stateChanged(request: DocumentationRequest, result: Deferred<DocumentationData?>, byLink: Boolean)
}
