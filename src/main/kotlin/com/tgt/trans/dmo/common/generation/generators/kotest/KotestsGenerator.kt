package com.tgt.trans.dmo.common.generation.generators.kotest

import com.tgt.trans.dmo.common.generation.common.makeSureFolderExistsFor
import com.tgt.trans.dmo.common.generation.mockk.generateAllMockks
import com.tgt.trans.dmo.common.generation.sample.PublicCallablesFactory
import com.tgt.trans.dmo.common.generation.sample.SampleInstanceFactory
import java.io.File
import kotlin.reflect.KClass

fun generateAllTests(klass: KClass<*>,
                     fileName: String = "src/test/kotlin/unit/generated/${klass.simpleName}Test.kt"
) {
    val codeSnippetFactory = SampleInstanceFactory()
    val callablesToTest = PublicCallablesFactory().callablesToTest(klass)
    val testSuite = KotestsSuiteFactory(codeSnippetFactory).generateKotests(klass, callablesToTest)
    val code = DefaultKotestGenerator2().generateCode(testSuite)
    makeSureFolderExistsFor(fileName)
    File(fileName).writeText(code)
}

fun generateAllTestsAndMockks(klass: KClass<*>,
    folder: String = "src/test/kotlin/unit/generated")
{
    generateAllTests(klass, "$folder/${klass.simpleName}Test.kt")
    generateAllMockks(klass, "$folder/Mockk${klass.simpleName}.kt")
}