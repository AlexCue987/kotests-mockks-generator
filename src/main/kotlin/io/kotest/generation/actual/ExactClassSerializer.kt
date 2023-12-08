package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import kotlin.reflect.KClass

class ExactClassSerializer(
    private val klass: KClass<*>,
    private val classesToImport: List<KClass<*>>,
    private val codeLineFactory: (Any) -> String
): SerializerVisitor {
    constructor(
        klass: KClass<*>,
        codeLineFactory: (Any) -> String
    ): this(klass, listOf(klass), codeLineFactory)

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addClassNames(classesToImport.mapNotNull { it.qualifiedName })
        val line = codeLineFactory(instance!!)
        buffer.addLine(line, isLast)
    }

    override fun canHandle(instance: Any?): Boolean =
        instance?.let {it::class == klass} ?: false
}

class ExactClassWithEmbeddedSerializer(
    private val klass: KClass<*>,
    private val classesToImport: List<KClass<*>>,
    private val serializer: SerializerVisitor
): SerializerVisitor {

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addClassNames(classesToImport.mapNotNull { it.qualifiedName })
        serializer.handle(instance, buffer, isLast)
    }

    override fun canHandle(instance: Any?): Boolean =
        instance?.let {it::class == klass} ?: false
}