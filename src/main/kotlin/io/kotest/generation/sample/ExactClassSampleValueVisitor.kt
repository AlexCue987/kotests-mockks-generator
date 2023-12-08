package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import kotlin.reflect.KClass

class ExactClassSampleValueVisitor(
    val klass: KClass<*>,
    val classesToImport: List<String>,
    val sampler: () -> String
): SampleSerializerVisitor {
    constructor(klass: KClass<*>, sampler: () -> String):
            this(klass, listOf(klass.qualifiedName!!), sampler)

    override fun canHandle(klass: KClass<*>) = this.klass == klass

    override fun handle(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addClassNames(classesToImport)
        buffer.addLine(sampler(), isLast)
    }
}