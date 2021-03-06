package com.intellij.codeInspection.tests.java

import com.intellij.codeInspection.tests.JUnitRuleInspectionTestBase
import com.intellij.jvm.analysis.JavaJvmAnalysisTestUtil
import com.intellij.testFramework.TestDataPath

private const val inspectionPath = "/codeInspection/junitrule"

@TestDataPath("\$CONTENT_ROOT/testData$inspectionPath")
class JavaJUnitRuleInspectionTest : JUnitRuleInspectionTestBase() {
  override fun getBasePath() = JavaJvmAnalysisTestUtil.TEST_DATA_PROJECT_RELATIVE_BASE_PATH + inspectionPath


  fun `test @Rule highlighting`() {
    myFixture.testHighlighting("RuleTest.java")
  }

  fun `test @Rule quickFixes`() {
    val quickfixes = myFixture.getAllQuickFixes("RuleQfTest.java")
    quickfixes.forEach { myFixture.launchAction(it) }
    myFixture.checkResultByFile("RuleQfTest.after.java")
  }

  fun `test @ClassRule highlighting`() {
    myFixture.testHighlighting("ClassRuleTest.java")
  }

  fun `test @ClassRule quickFixes`() {
    val quickfixes = myFixture.getAllQuickFixes("ClassRuleQfTest.java")
    quickfixes.forEach { myFixture.launchAction(it) }
    myFixture.checkResultByFile("ClassRuleQfTest.after.java")
  }
}