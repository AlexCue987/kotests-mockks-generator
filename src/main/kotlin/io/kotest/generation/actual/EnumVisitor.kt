package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.common.isEnum
import io.kotest.generation.common.lineTerminator
import io.kotest.generation.common.toKotlinName

class EnumVisitor: SerializerVisitor {
    override fun canHandle(instance: Any?): Boolean = instance?.let { isEnum(it::class) } ?: false

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        val klass = instance!!::class
        val line = "${klass.simpleName}.${instance.toString().toKotlinName()}"
        buffer.addLine(line, isLast)
        buffer.addClassName(klass.qualifiedName!!)
    }
}