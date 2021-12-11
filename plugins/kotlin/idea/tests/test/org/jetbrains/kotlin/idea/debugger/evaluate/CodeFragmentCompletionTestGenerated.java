// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.debugger.evaluate;

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
@TestMetadata("../../completion/tests/testData/basic/codeFragments")
public class CodeFragmentCompletionTestGenerated extends AbstractCodeFragmentCompletionTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("blockCodeFragment.kt")
    public void testBlockCodeFragment() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/blockCodeFragment.kt");
    }

    @TestMetadata("classHeader.kt")
    public void testClassHeader() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/classHeader.kt");
    }

    @TestMetadata("elementAt.kt")
    public void testElementAt() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/elementAt.kt");
    }

    @TestMetadata("elementAtFirstInBlock.kt")
    public void testElementAtFirstInBlock() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/elementAtFirstInBlock.kt");
    }

    @TestMetadata("localVal.kt")
    public void testLocalVal() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/localVal.kt");
    }

    @TestMetadata("localVariables.kt")
    public void testLocalVariables() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/localVariables.kt");
    }

    @TestMetadata("localVariablesOnReturn.kt")
    public void testLocalVariablesOnReturn() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/localVariablesOnReturn.kt");
    }

    @TestMetadata("noDuplicatesForSyntheticProperties.kt")
    public void testNoDuplicatesForSyntheticProperties() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/noDuplicatesForSyntheticProperties.kt");
    }

    @TestMetadata("privatesInSecondPressCompletion.kt")
    public void testPrivatesInSecondPressCompletion() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/privatesInSecondPressCompletion.kt");
    }

    @TestMetadata("syntheticFieldProperties.kt")
    public void testSyntheticFieldProperties() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/syntheticFieldProperties.kt");
    }

    @TestMetadata("topLevel.kt")
    public void testTopLevel() throws Exception {
        runTest("../../completion/tests/testData/basic/codeFragments/topLevel.kt");
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("../../completion/tests/testData/basic/codeFragments/runtimeType")
    public static class RuntimeType extends AbstractCodeFragmentCompletionTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("castWithGenerics.kt")
        public void testCastWithGenerics() throws Exception {
            runTest("../../completion/tests/testData/basic/codeFragments/runtimeType/castWithGenerics.kt");
        }

        @TestMetadata("complexHierarchy.kt")
        public void testComplexHierarchy() throws Exception {
            runTest("../../completion/tests/testData/basic/codeFragments/runtimeType/complexHierarchy.kt");
        }

        @TestMetadata("extensionMethod.kt")
        public void testExtensionMethod() throws Exception {
            runTest("../../completion/tests/testData/basic/codeFragments/runtimeType/extensionMethod.kt");
        }

        @TestMetadata("notImportedExtension.kt")
        public void testNotImportedExtension() throws Exception {
            runTest("../../completion/tests/testData/basic/codeFragments/runtimeType/notImportedExtension.kt");
        }

        @TestMetadata("runtimeCast.kt")
        public void testRuntimeCast() throws Exception {
            runTest("../../completion/tests/testData/basic/codeFragments/runtimeType/runtimeCast.kt");
        }

        @TestMetadata("smartCompletion.kt")
        public void testSmartCompletion() throws Exception {
            runTest("../../completion/tests/testData/basic/codeFragments/runtimeType/smartCompletion.kt");
        }
    }
}