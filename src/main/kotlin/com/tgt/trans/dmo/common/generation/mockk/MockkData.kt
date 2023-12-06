package com.tgt.trans.dmo.common.generation.mockk

import com.tgt.trans.dmo.common.generation.common.HasImports
import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import com.tgt.trans.dmo.common.generation.common.HasClassName
import com.tgt.trans.dmo.common.generation.common.packageName
import kotlin.reflect.KClass

data class MockkData(
    private val klass: KClass<*>,
    val mockksForMethods: List<CodeSnippet>
    ): HasClassName, HasImports {
    override val simpleName: String
        get() = klass.simpleName!!

    override val qualifiedName: String
        get() = klass.qualifiedName!!

    override val packageName: String
        get() = packageName(klass)

    override fun qualifiedNames(): Collection<String> {
        return listOf(klass.qualifiedName!!, *(mockksForMethods.asSequence().map { it.qualifiedNames() }.flatten().toList().toTypedArray())).distinct()
    }
}