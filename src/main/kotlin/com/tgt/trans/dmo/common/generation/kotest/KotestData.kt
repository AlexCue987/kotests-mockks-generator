package com.tgt.trans.dmo.common.generation.kotest

import com.tgt.trans.dmo.common.generation.common.CodeSnippetFactory
import com.tgt.trans.dmo.common.generation.common.HasClassName
import com.tgt.trans.dmo.common.generation.common.ImportsGenerator
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

data class KotestData(
    val systemToTest: SystemToTest,
    val testsForMethods: List<MethodToTest>
): HasClassName by systemToTest{
    val classesToImport: List<String> =
        ImportsGenerator().generate(systemToTest, *(testsForMethods.toTypedArray()))
}

class KotestDataFactory {
    fun get(
        codeSnippetFactory: CodeSnippetFactory,
        klass: KClass<*>,
        vararg methods: KFunction<*>
    ): KotestData {
        val systemToTest = SystemToTest(klass, codeSnippetFactory)
        val methodsToTest = methods.asSequence().map { MethodToTest(it, codeSnippetFactory) }.toList()
        return KotestData(systemToTest, methodsToTest)
    }
}