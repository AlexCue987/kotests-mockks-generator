package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.common.firstEnumValue
import io.kotest.generation.common.isEnum
import kotlin.reflect.KClass

class EnumSampleValueVisitor: SampleSerializerVisitor {
    override fun canHandle(klass: KClass<*>): Boolean = isEnum(klass)

    override fun handle(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addClassName(klass.qualifiedName!!)
        buffer.addLine("${klass.simpleName}.${firstEnumValue(klass)}", isLast)
    }
}