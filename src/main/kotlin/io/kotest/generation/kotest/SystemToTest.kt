package io.kotest.generation.kotest

import io.kotest.generation.common.*
import kotlin.reflect.KClass

class SystemToTest(
    private val klass: KClass<*>,
    codeSnippetFactory: CodeSnippetFactory
): HasSourceCode, HasClassName {
    private val codeSnippet = sampleInstance(klass, codeSnippetFactory)

    override val simpleName: String
        get() = klass.simpleName!!

    override val qualifiedName: String
        get() = klass.qualifiedName!!

    override val packageName: String
        get() = packageName(klass)

    override fun sourceCode(): Collection<String> {
        return codeSnippet.sourceCode()
    }

    override fun qualifiedNames(): Collection<String> {
        return listOf(klass.qualifiedName!!, *(codeSnippet.qualifiedNames().toTypedArray()))
    }
}

