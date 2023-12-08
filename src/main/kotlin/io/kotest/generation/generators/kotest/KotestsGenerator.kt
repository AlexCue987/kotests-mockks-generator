package io.kotest.generation.generators.kotest

import io.kotest.generation.common.makeSureFolderExistsFor
import io.kotest.generation.mockk.generateAllMockks
import io.kotest.generation.sample.PublicCallablesFactory
import io.kotest.generation.sample.SampleInstanceFactory
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