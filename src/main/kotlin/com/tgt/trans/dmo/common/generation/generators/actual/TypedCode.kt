package com.tgt.trans.dmo.common.generation.generators.actual

import com.tgt.trans.dmo.common.generation.common.CodeSnippet

data class TypedCode(
    val simpleName: String,
    val qualifiedName: String,
    val code: CodeSnippet
    )