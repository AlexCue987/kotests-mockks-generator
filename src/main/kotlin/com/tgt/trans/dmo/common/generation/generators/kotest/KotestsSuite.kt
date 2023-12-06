package com.tgt.trans.dmo.common.generation.generators.kotest

import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import com.tgt.trans.dmo.common.generation.generators.common.InstanceCodeSnippet

data class KotestsSuite(
    val systemToTest: InstanceCodeSnippet,
    val tests: List<CodeSnippet>
) {
    val simpleName = systemToTest.simpleName
    val packageName = systemToTest.packageName
    val classesToImport = (tests.map {it.qualifiedNames()}.flatten() +
        systemToTest.qualifiedNames()).distinct()
}

