/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.codeInsight.editorActions.smartEnter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;

@SuppressWarnings({"HardCodedStringLiteral"})
public class SwitchExpressionFixer implements Fixer {
  @Override
  public void apply(Editor editor, JavaSmartEnterProcessor processor, PsiElement psiElement) throws IncorrectOperationException {
    if (psiElement instanceof PsiSwitchBlock) {
      final Document doc = editor.getDocument();
      final PsiSwitchBlock switchStatement = (PsiSwitchBlock)psiElement;
      final PsiJavaToken rParenth = switchStatement.getRParenth();
      final PsiJavaToken lParenth = switchStatement.getLParenth();
      final PsiExpression condition = switchStatement.getExpression();

      if (condition == null) {
        if (lParenth == null || rParenth == null) {
          int stopOffset = doc.getLineEndOffset(doc.getLineNumber(switchStatement.getTextRange().getStartOffset()));
          final PsiCodeBlock block = switchStatement.getBody();
          if (block != null) {
            stopOffset = Math.min(stopOffset, block.getTextRange().getStartOffset());
          }
          doc.replaceString(switchStatement.getTextRange().getStartOffset(), stopOffset, "switch ()");
        } else {
          processor.registerUnresolvedError(lParenth.getTextRange().getEndOffset());
        }
      } else if (rParenth == null) {
        doc.insertString(condition.getTextRange().getEndOffset(), ")");
      }
    }
  }
}