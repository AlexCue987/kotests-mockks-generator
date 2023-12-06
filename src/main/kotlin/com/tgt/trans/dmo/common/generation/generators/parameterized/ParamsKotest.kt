package com.tgt.trans.dmo.common.generation.generators.parameterized

import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import com.tgt.trans.dmo.common.generation.generators.common.InstanceCodeSnippet

data class ParamsKotest(
    val systemToTest: InstanceCodeSnippet,
    val methodName: String,
    val methodParams: String,
    val tests: List<CodeSnippet>,
    val hasExceptions: Boolean
) {
    val simpleName = systemToTest.simpleName
    val packageName = systemToTest.packageName
    val classesToImport = (tests.map {it.qualifiedNames()}.flatten() +
            listOf(
                CallOutcome::class,
                CallException::class,
                CallResult::class
            ).map { it.qualifiedName!! } +
        systemToTest.qualifiedName).distinct()
}

