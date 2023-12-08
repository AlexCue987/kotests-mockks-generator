package io.kotest.generation.generators.actual

import io.kotest.generation.common.CodeSnippet

data class TypedCode(
    val simpleName: String,
    val qualifiedName: String,
    val code: CodeSnippet
    )