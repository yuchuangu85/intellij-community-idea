// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.execution.junit.codeInsight;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public class Junit5MalformedParameterizedAddTestInstanceTest extends LightJavaCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return "/plugins/junit/testData/codeInsight/junit5malformed/addTestInstanceToClassFix/";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    JUnit5TestFrameworkSetupUtil.setupJUnit5Library(myFixture);
    myFixture.enableInspections(new JUnit5MalformedParameterizedInspection());
  }

  @NotNull
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return JAVA_8;
  }

  public void testTest() { doTest(); }

  private void doTest() {
    final String name = getTestName(false);
    myFixture.configureByFile(name + ".java");
    myFixture.launchAction(myFixture.findSingleIntention("Annotate class 'Test' as @TestInstance"));
    myFixture.checkResultByFile(name + ".after.java");
  }
}
