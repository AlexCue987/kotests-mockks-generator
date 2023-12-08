package io.kotest.generation.kotest

import io.kotest.generation.common.HasSourceCode
import io.kotest.generation.common.CodeSnippetFactory
import kotlin.reflect.KFunction

class MethodToTest(
    method: KFunction<*>,
    codeSnippetFactory: CodeSnippetFactory
): HasSourceCode {
    private val codeSnippet = unitKotest(method, codeSnippetFactory)

    override fun sourceCode(): Collection<String> = codeSnippet.sourceCode()

    override fun qualifiedNames(): Collection<String> = codeSnippet.qualifiedNames()
}