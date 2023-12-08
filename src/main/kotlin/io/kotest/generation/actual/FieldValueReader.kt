package io.kotest.generation.actual

import io.kotest.generation.common.kTypeToKClass
import java.lang.reflect.Parameter
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType

class FieldValueReader {
    fun primaryConstructorParameters(
        instance: Any,
        exposePrivateFields: Boolean = true
    ): List<FieldValue> {
        val kClass = instance::class
        val constructor = kClass.primaryConstructor!!
        return constructorParameters(constructor, exposePrivateFields, instance)
    }

    fun constructorParameters(
        constructor: KFunction<Any>,
        exposePrivateFields: Boolean,
        instance: Any
    ): List<FieldValue> {
        val kClass = instance::class
        val parameters = constructor.parameters
        val fields = kClass.memberProperties.filter { field ->
            (field.visibility == KVisibility.PUBLIC || exposePrivateFields)
        }
        return parameters.asSequence().mapNotNull { parameter ->
            val field = fields.find { field ->
                field.name == parameter.name
            }
            field?.let {
                val value = field?.let { fieldValue(instance, field) }
                FieldValue(parameter.name!!, value, kTypeToKClass(field.returnType), field)
            }
        }.toList()
    }

    fun fieldValue(instance: Any, field: KProperty1<*, *>): Any? {
        field.isAccessible = true
        return field.getter.call(instance)
    }
}

data class FieldValue(
    val name: String,
    val value: Any?,
    val klass: KClass<*>,
    val field: KProperty1<*, *>
)