// This is a generated file. Not intended for manual editing.
package com.intellij.sh.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.intellij.sh.ShTypes.*;
import com.intellij.sh.psi.*;

public class ShPostOperationImpl extends ShOperationImpl implements ShPostOperation {

  public ShPostOperationImpl(ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ShVisitor visitor) {
    visitor.visitPostOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ShVisitor) accept((ShVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ShOperation getOperation() {
    return findNotNullChildByClass(ShOperation.class);
  }

  @Override
  @Nullable
  public PsiElement getMinusMinus() {
    return findChildByType(MINUS_MINUS);
  }

  @Override
  @Nullable
  public PsiElement getPlusPlus() {
    return findChildByType(PLUS_PLUS);
  }

}