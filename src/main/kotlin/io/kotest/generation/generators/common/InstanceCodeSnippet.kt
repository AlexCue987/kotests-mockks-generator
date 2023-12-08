package io.kotest.generation.generators.common

import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.common.HasClassName
import io.kotest.generation.common.HasSourceCode
import io.kotest.generation.common.packageName
import kotlin.reflect.KClass

data class InstanceCodeSnippet(
    private val klass: KClass<*>,
    val codeSnippet: CodeSnippet
    ): HasSourceCode, HasClassName {
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