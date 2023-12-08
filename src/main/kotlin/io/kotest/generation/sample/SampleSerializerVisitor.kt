package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import kotlin.reflect.KClass

interface SerializerVisitor2<K> {
    fun serialize(
        klass: K,
        buffer: CodeSnippet,
        isLast: Boolean
    ): Boolean {
        if(canHandle((klass))) {
            handle(klass, buffer, isLast)
            return true
        }
        return false
    }

    fun canHandle(klass: K): Boolean

    fun handle(
        klass: K,
        buffer: CodeSnippet,
        isLast: Boolean
    )
}

interface SampleSerializerVisitor: SerializerVisitor2<KClass<*>>