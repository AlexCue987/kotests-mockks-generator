package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet

class SerializersList(
    private val serializers: List<SerializerVisitor>
): SerializerVisitor {
    override fun serialize(
        instance: Any?,
        buffer: CodeSnippet,
        isLast: Boolean
    ): Boolean {
        return serializers.any { it.serialize(instance, buffer, isLast) }
    }

    override fun canHandle(instance: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        TODO("Not yet implemented")
    }
}