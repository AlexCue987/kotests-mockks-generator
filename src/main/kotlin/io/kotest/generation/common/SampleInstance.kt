package io.kotest.generation.common

import io.kotest.generation.kotest.onlyValue
import io.kotest.generation.kotest.parameterToValueAssignment
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