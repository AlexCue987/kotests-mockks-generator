package com.tgt.trans.dmo.common.generation.kotest

import com.tgt.trans.dmo.common.generation.common.HasSourceCode
import com.tgt.trans.dmo.common.generation.common.CodeSnippetFactory
import kotlin.reflect.KFunction

class MethodToTest(
    method: KFunction<*>,
    codeSnippetFactory: CodeSnippetFactory
): HasSourceCode {
    private val codeSnippet = unitKotest(method, codeSnippetFactory)

    override fun sourceCode(): Collection<String> = codeSnippet.sourceCode()

    override fun qualifiedNames(): Collection<String> = codeSnippet.qualifiedNames()
}