package io.kotest.generation.generators.kotest

import io.kotest.generation.common.*
import io.kotest.generation.generators.common.InstanceCodeSnippet
import io.kotest.generation.sample.PrimaryConstructorFieldsSampleVisitorType
import io.kotest.generation.sample.SampleInstanceFactory
import kotlin.reflect.*
import kotlin.reflect.full.primaryConstructor

class KotestsSuiteFactory(
    val codeSnippetFactory: SampleInstanceFactory = SampleInstanceFactory()
) {
   fun generateKotests(
        klass: KClass<*>,
        callablesToTest: List<KCallable<*>>
    ): KotestsSuite {
        val systemToTest = codeSnippetFactory
            .withType(newType = PrimaryConstructorFieldsSampleVisitorType.VALUE,
                newNestedType = PrimaryConstructorFieldsSampleVisitorType.`SYSTEM-TO-TEST`)
            .serializeSampleValue(klass)
        val unitTests = callablesToTest.map {
            unitKotest(it)
        }
        return KotestsSuite(InstanceCodeSnippet(klass, systemToTest), unitTests)
    }

    fun unitKotest(method: KCallable<*>): CodeSnippet {
        val codeSnippet = CodeSnippet()
        codeSnippet.addLine("\"${method.name} works\".config(enabled = false) {")
        addMethodInvocationToKotest(codeSnippet, method)
        if(!isVoid(method)) {
            addAssertionsToKotest(codeSnippet, method)
        }
        codeSnippet.addLine("}")
        return codeSnippet
    }

    fun addAssertionsToKotest(
        codeSnippet: CodeSnippet,
        method: KCallable<*>
    ) {
        val returnedClass = kTypeToKClass(method.returnType)
        if(returnedClass.isData) {
            codeSnippet.addLine("// Keep either these assertions")
            addAssertionsPerFieldToKotest(codeSnippet, returnedClass, KotestsSuiteFactory::assertMatchesSampleValue)
            codeSnippet.addLine("// or these assertions")
            addExpected(codeSnippet, returnedClass)
            addAssertionsPerFieldToKotest(codeSnippet, returnedClass, KotestsSuiteFactory::assertMatchesExpected)
        } else {
            addOneAssertionToKotest(codeSnippet, returnedClass)
        }
    }

    private fun addExpected(
        codeSnippet: CodeSnippet,
        returnedClass: KClass<*>
    ) {
        codeSnippet.addText("val expected = ")
        codeSnippetFactory.addValue(returnedClass, codeSnippet)
    }

    private fun addAssertionsPerFieldToKotest(
        codeSnippet: CodeSnippet,
        returnedClass: KClass<*>,
        kFunction5: KFunction5<KotestsSuiteFactory, KClass<*>, CodeSnippet, String, String, Unit>
    ) {
        codeSnippet.addLine("assertSoftly {")
        val prefix = ""
        sprayKlassToAssertions(returnedClass, codeSnippet, prefix,
            kFunction5
        )
        codeSnippet.addLine("}")
    }

    private fun assertMatchesSampleValue(klass: KClass<*>,
                                    codeSnippet: CodeSnippet,
                                         fieldName: String,
                                         prefix: String
    ) = codeSnippetFactory.addValue(klass, codeSnippet, true)


    private fun assertMatchesExpected(klass: KClass<*>,
                                         codeSnippet: CodeSnippet,
                                      fieldName: String,
                                      prefix: String
    ) = codeSnippet.addLine(" expected${if(prefix.isEmpty()) "." else ".$prefix"}$fieldName", true)

    private fun sprayKlassToAssertions(
        returnedClass: KClass<*>,
        codeSnippet: CodeSnippet,
        prefix: String,
        expectedValueAdder: KFunction5<KotestsSuiteFactory, KClass<*>, CodeSnippet, String, String, Unit>
    ) {
        val returnedInstanceParameters = returnedClass.primaryConstructor?.parameters ?: listOf()
        returnedInstanceParameters.forEach { parameter ->
            val klass = kParameterToKClass(parameter)
            if(isDataClass(klass)) {
                sprayKlassToAssertions(klass, codeSnippet, "$prefix${parameter.name}.", expectedValueAdder)
            } else {
                codeSnippet.addText("actual.$prefix${parameter.name} shouldBe ")
                expectedValueAdder(this, klass, codeSnippet, parameter.name!!, prefix)
            }
        }
    }

    private fun addOneAssertionToKotest(
        codeSnippet: CodeSnippet,
        returnedClass: KClass<*>
    ) {
        codeSnippet.addText("actual shouldBe ")
        codeSnippetFactory.addValue(returnedClass, codeSnippet)
    }

    fun addMethodInvocationToKotest(
        codeSnippet: CodeSnippet,
        method: KCallable<*>
    ) {
        when {
            isProperty(method) ->
                addPropertyInvocation(codeSnippet, method as KProperty)
            isFunction(method) ->
                addFunctionInvocation(codeSnippet, method as KFunction)
            else ->
                throw IllegalArgumentException("Unsupported callable: ${method.name}")
        }
    }

    fun addFunctionInvocation(
        codeSnippet: CodeSnippet,
        method: KFunction<*>
    ) {
        val methodParameters = methodParameters(method)
        val hasParameters = methodParameters.isNotEmpty()
        codeSnippet.addLine("${if(isVoid(method)) "" else "val actual = "}systemToTest.${method.name}${if(hasParameters) "(" else "()"}")
        methodParameters.forEachIndexed { index, parameter ->
            codeSnippet.addText("${parameter.name} = ")
            codeSnippetFactory.addValue(kParameterToKClass(parameter), codeSnippet, isLastIndex(index, methodParameters))
        }
        if (hasParameters) {
            codeSnippet.addLine(")")
        }
    }

    fun addPropertyInvocation(
        codeSnippet: CodeSnippet,
        method: KProperty<*>
    ) {
        codeSnippet.addLine("val actual = systemToTest.${method.name}")
    }
}