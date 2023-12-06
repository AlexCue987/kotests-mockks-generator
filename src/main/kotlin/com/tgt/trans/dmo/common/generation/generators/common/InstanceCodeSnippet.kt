package com.tgt.trans.dmo.common.generation.generators.common

import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import com.tgt.trans.dmo.common.generation.common.HasClassName
import com.tgt.trans.dmo.common.generation.common.HasSourceCode
import com.tgt.trans.dmo.common.generation.common.packageName
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