package com.tgt.trans.dmo.common.generation.common

import com.tgt.trans.dmo.common.generation.kotest.onlyValue
import com.tgt.trans.dmo.common.generation.kotest.parameterToValueAssignment
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

fun CodeSnippetFactory.sampleInstance(klass: KClass<*>): CodeSnippet =
    sampleInstance(klass, this)

fun sampleInstance(klass: KClass<*>, codeSnippetFactory: ParametersInitializer): CodeSnippet {
    val codeSnippet = CodeSnippet()
    codeSnippet.addLine("${klass.simpleName}(")
    codeSnippet.addClassName(klass.qualifiedName!!)
    val parameters = klass.primaryConstructor?.parameters ?: listOf()
    codeSnippetFactory.initializeParameters(parameters, codeSnippet, ::parameterToValueAssignment)
    codeSnippet.addLine(")")
    return codeSnippet
}

fun sampleValue(klass: KClass<*>, codeSnippetFactory: CodeSnippetFactory): CodeSnippet {
    return if (klass.isData) {
        sampleInstance(klass, codeSnippetFactory)
    } else {
        val codeSnippet = CodeSnippet()
        codeSnippetFactory.addValue(klass, codeSnippet, true, ::onlyValue, "")
        codeSnippet
    }
}