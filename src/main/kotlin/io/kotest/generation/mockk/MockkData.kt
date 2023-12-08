package io.kotest.generation.mockk

import io.kotest.generation.common.HasImports
import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.common.HasClassName
import io.kotest.generation.common.packageName
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