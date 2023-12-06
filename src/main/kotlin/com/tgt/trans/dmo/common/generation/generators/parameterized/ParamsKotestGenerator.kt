package com.tgt.trans.dmo.common.generation.generators.parameterized

import com.tgt.trans.dmo.common.generation.actual.*
import com.tgt.trans.dmo.common.generation.common.makeSureFolderExistsFor
import java.io.File
import kotlin.reflect.KCallable

class ParamsKotestGenerator() {

    fun generateCode(data: ParamsKotest): String {
        return """
package ${data.packageName}

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.IsolationMode

${data.classesToImport.distinct().sorted().joinToString("\n") { "import $it" }}

// generated by com.tgt.trans.dmo.common:kotests-generator
class ${data.simpleName}ParamsKotest: StringSpec() {
override fun isolationMode() = IsolationMode.InstancePerTest

private val systemToTest = ${data.systemToTest.sourceCode().joinToString("\n")}

init {
        "parameterized test" {
            listOf(
        //TODO: add header and enter clues
                ${data.tests.joinToString(",\n") { it.sourceCodeAsOneString() }}
            ).forAll { (${data.methodParams}, 
                         expectedOutcome, 
                         clue) ->
${if(data.hasExceptions) testAllOutcomesBody(data) else testOnlySuccessBody(data)}                         
            }
        }

}
}
        """.trimIndent()
    }

    fun testOnlySuccessBody(data: ParamsKotest): String {
        return """
                withClue(clue) {
                    systemToTest.${data.methodName}(${data.methodParams}) shouldBe expectedOutcome
                }            
        """.trimIndent()
    }

    fun testAllOutcomesBody(data: ParamsKotest): String {
        return """
                withClue(clue) {
                    when(expectedOutcome) {
                        is CallResult -> systemToTest.${data.methodName}(${data.methodParams}) shouldBe expectedOutcome.result
                        is CallException -> {
                            val actual = shouldThrow<Exception> { systemToTest.${data.methodName}(${data.methodParams}) }
                            actual::class shouldBe expectedOutcome.throwable::class
                        }  
                    }
                }            
        """.trimIndent()
    }
}



fun generateParamsKotest(
    filename: String,
    instanceToTest: Any,
    callableToTest: KCallable<*>,
    params: List<List<Any?>>
) {
    val customCallResultSerializer = ExactClassWithEmbeddedSerializer(
        CallResult::class,
        classesToImport = listOf(),
        serializer = PrimaryConstructorFieldsVisitor(
            ActualInstanceFactory(),
            SerializeToInstanceStrategy(suffix = " as CallOutcome")
        )
    )

    val customCallExceptionSerializer = ExactClassWithEmbeddedSerializer(
        CallException::class,
        classesToImport = listOf(),
        serializer = PrimaryConstructorFieldsVisitor(
            ActualInstanceFactory(),
            SerializeToInstanceStrategy(suffix = " as CallOutcome")
        )
    )

    val customizedFactory = ActualInstanceFactory(
        customSerializers = listOf(customCallResultSerializer, customCallExceptionSerializer)
    )
    val paramsKotestFactory = ParamsKotestFactory(customizedFactory)
    val data = paramsKotestFactory.generateParamsKotest(
        instanceToTest, callableToTest, params
    )
    val code = ParamsKotestGenerator().generateCode(data)
    makeSureFolderExistsFor(filename)
    File(filename).writeText(code)
}