package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import kotlin.reflect.KClass

class SampleInstanceFactory(
    private val customVisitors: List<SampleSerializerVisitor> = listOf(),
    type: PrimaryConstructorFieldsSampleVisitorType = PrimaryConstructorFieldsSampleVisitorType.VALUE,
    nestedType: PrimaryConstructorFieldsSampleVisitorType = PrimaryConstructorFieldsSampleVisitorType.VALUE
) {
    private val visitors = listOf(
        CustomizedSampleValueVisitor(customVisitors),
        DefaultSampleValueVisitor(),
        EnumSampleValueVisitor(),
        SampleCollectionVisitor(),
        PrimaryConstructorFieldsSampleVisitor(this, type, nestedType),
        LastResortSimpleMockVisitor()
    )

    fun withType(newType: PrimaryConstructorFieldsSampleVisitorType,
                 newNestedType: PrimaryConstructorFieldsSampleVisitorType
    ) =
        SampleInstanceFactory(this.customVisitors,
            type = newType,
            nestedType = newNestedType)

    fun serializeSampleValue(klass: KClass<*>): CodeSnippet {
        val buffer = CodeSnippet()
        addValue(
            klass, buffer, true, ""
        )
        return buffer
    }

    fun addValue(
        klass: KClass<*>,
        buffer: CodeSnippet,
        isLast: Boolean = true,
        prefix: String = ""
    ) {
        buffer.addText(prefix)
        if (visitors.any { visitor ->
                visitor.serialize(klass, buffer, isLast)
            }) {
            return
        }
        buffer.addLine("// Cannot serialize sample value for ${klass.qualifiedName}")
    }
}