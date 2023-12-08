package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import kotlin.reflect.KClass

class DescendantOfClassSerializer(
    private val klass: KClass<*>,
    private val qualifiedNames: List<String>,
    private val codeLineFactory: (Any) -> String
): SerializerVisitor {
    constructor(klass: KClass<*>, codeLineFactory: (Any) -> String):
            this(klass, listOf(klass.qualifiedName!!), codeLineFactory)

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addClassNames(qualifiedNames)
        val line = codeLineFactory(instance!!)
        buffer.addLine(line, isLast)
    }

    override fun canHandle(instance: Any?): Boolean =
        instance?.let { klass.java.isAssignableFrom(instance::class.java) } ?: false
}