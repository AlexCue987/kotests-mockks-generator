package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet

class NullValueVisitor() : SerializerVisitor {
    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addLine("null", isLast)
    }

    override fun canHandle(instance: Any?) = (instance == null)
}