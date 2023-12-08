package io.kotest.generation.actual

import io.kotest.generation.common.*
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*
import kotlin.reflect.KClass

class ActualInstanceFactory(
    val customSerializers: List<SerializerVisitor> = listOf(),
    serializeByFieldsStrategy: SerializeByFieldsStrategy = SerializeToInstanceStrategy()
) {
    private val nullValueVisitor = NullValueVisitor()
    private val customSerializer = SerializersList(customSerializers)
    private val defaultClassesVisitor =  DefaultClassesSerializerFactory.defaultClassesSerializer()
    private val enumVisitor = EnumVisitor()
    private val listOrSetVisitor = SerializeCollectionVisitor(this)
    private val mapVisitor = SerializeMapVisitor(this)
    private val primaryConstructorFieldsVisitor = PrimaryConstructorFieldsVisitor(this, serializeByFieldsStrategy)

    private val visitors = listOf<SerializerVisitor>(
        nullValueVisitor,
        customSerializer,
        defaultClassesVisitor,
        enumVisitor,
        listOrSetVisitor,
        mapVisitor,
        primaryConstructorFieldsVisitor
    )

    fun serializeInstance(
        instance: Any?
    ): CodeSnippet {
        val buffer = CodeSnippet()
        addValue(
            instance, buffer, true, ""
        )
        return buffer
    }

    fun addValue(
        instance: Any?,
        buffer: CodeSnippet,
        isLast: Boolean,
        prefix: String = ""
    ) {
        buffer.addText(prefix)
        if (visitors.any { visitor ->
                visitor.serialize(instance, buffer, isLast)
            }) {
            return
        }
        buffer.addLine("// Cannot serialize $instance")
    }

    val innerLevelFactory: ActualInstanceFactory
     by lazy {
        ActualInstanceFactory(
            customSerializers,
            SerializeToInstanceStrategy()
        )
    }
}

