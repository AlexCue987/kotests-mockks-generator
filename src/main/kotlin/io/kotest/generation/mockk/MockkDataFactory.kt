package io.kotest.generation.mockk

import io.kotest.generation.common.CodeSnippetFactory
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class MockkDataFactory {
    fun get(
        factory: CodeSnippetFactory,
        klass: KClass<*>,
        vararg methods: KCallable<*>
    ): MockkData {
        val methodsToMockk = methods.map { mockkedMethod(it, factory) }
        return MockkData(klass, methodsToMockk)
    }
}