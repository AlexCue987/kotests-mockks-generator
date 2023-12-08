package io.kotest.generation.generators.kotest

import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.generators.common.InstanceCodeSnippet

data class KotestsSuite(
    val systemToTest: InstanceCodeSnippet,
    val tests: List<CodeSnippet>
) {
    val simpleName = systemToTest.simpleName
    val packageName = systemToTest.packageName
    val classesToImport = (tests.map {it.qualifiedNames()}.flatten() +
        systemToTest.qualifiedNames()).distinct()
}

