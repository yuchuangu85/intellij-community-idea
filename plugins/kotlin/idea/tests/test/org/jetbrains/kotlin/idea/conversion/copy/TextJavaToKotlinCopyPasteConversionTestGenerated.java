// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.conversion.copy;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.jetbrains.kotlin.test.TestRoot;
import org.junit.runner.RunWith;

/**
 * This class is generated by {@link org.jetbrains.kotlin.testGenerator.generator.TestGenerator}.
 * DO NOT MODIFY MANUALLY.
 */
@SuppressWarnings("all")
@TestRoot("idea/tests")
@TestDataPath("$CONTENT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
@TestMetadata("testData/copyPaste/plainTextConversion")
public class TextJavaToKotlinCopyPasteConversionTestGenerated extends AbstractTextJavaToKotlinCopyPasteConversionTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("AsExpression.txt")
    public void testAsExpression() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/AsExpression.txt");
    }

    @TestMetadata("AsExpressionBody.txt")
    public void testAsExpressionBody() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/AsExpressionBody.txt");
    }

    @TestMetadata("ImportFromTarget.txt")
    public void testImportFromTarget() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/ImportFromTarget.txt");
    }

    @TestMetadata("ImportResolve.txt")
    public void testImportResolve() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/ImportResolve.txt");
    }

    @TestMetadata("InsideIdentifier.txt")
    public void testInsideIdentifier() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/InsideIdentifier.txt");
    }

    @TestMetadata("IntoComment.txt")
    public void testIntoComment() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/IntoComment.txt");
    }

    @TestMetadata("IntoRawStringLiteral.txt")
    public void testIntoRawStringLiteral() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/IntoRawStringLiteral.txt");
    }

    @TestMetadata("IntoStringLiteral.txt")
    public void testIntoStringLiteral() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/IntoStringLiteral.txt");
    }

    @TestMetadata("KT13529.txt")
    public void testKT13529() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/KT13529.txt");
    }

    @TestMetadata("KT13529_1.txt")
    public void testKT13529_1() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/KT13529_1.txt");
    }

    @TestMetadata("MembersIntoClass.txt")
    public void testMembersIntoClass() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/MembersIntoClass.txt");
    }

    @TestMetadata("MembersToTopLevel.txt")
    public void testMembersToTopLevel() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/MembersToTopLevel.txt");
    }

    @TestMetadata("Override.txt")
    public void testOverride() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/Override.txt");
    }

    @TestMetadata("OverrideInterface.txt")
    public void testOverrideInterface() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/OverrideInterface.txt");
    }

    @TestMetadata("PostProcessing.txt")
    public void testPostProcessing() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/PostProcessing.txt");
    }

    @TestMetadata("StatementsIntoFunction.txt")
    public void testStatementsIntoFunction() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/StatementsIntoFunction.txt");
    }

    @TestMetadata("WholeFile.txt")
    public void testWholeFile() throws Exception {
        runTest("testData/copyPaste/plainTextConversion/WholeFile.txt");
    }
}