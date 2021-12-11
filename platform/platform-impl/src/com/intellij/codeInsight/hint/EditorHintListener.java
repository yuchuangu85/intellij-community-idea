// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.codeInsight.hint;

import com.intellij.openapi.project.Project;
import com.intellij.ui.LightweightHint;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

public interface EditorHintListener {
  /**
   * Notification about showing editor hints.
   */
  Topic<EditorHintListener> TOPIC = new Topic<>(EditorHintListener.class, Topic.BroadcastDirection.TO_DIRECT_CHILDREN);

  void hintShown(Project project, @NotNull LightweightHint hint, int flags);
}