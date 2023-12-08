package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.common.isDataClass
import io.kotest.generation.common.kTypeToKClass
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.primaryConstructor

class PrimaryConstructorFieldsSampleVisitor(
    private val sampleInstanceFactory: SampleInstanceFactory,
    private val type: PrimaryConstructorFieldsSampleVisitorType = PrimaryConstructorFieldsSampleVisitorType.VALUE,
    private val nestedType: PrimaryConstructorFieldsSampleVisitorType = PrimaryConstructorFieldsSampleVisitorType.VALUE
)
    : SampleSerializerVisitor {
    override fun canHandle(klass: KClass<*>): Boolean = when(type) {
        PrimaryConstructorFieldsSampleVisitorType.VALUE -> klass.primaryConstructor?.let {
            it.visibility == KVisibility.PUBLIC
        } ?: false
        else -> isDataClass(klass)
    }

    override fun handle(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addLine("${klass.simpleName}(")
        initalizeFields(klass, buffer)
        buffer.addLine(")", isLast)
        buffer.addClassName(klass.qualifiedName!!)
    }

    private fun initalizeFields(klass: KClass<*>, buffer: CodeSnippet) {
        val parameters = klass.primaryConstructor!!.parameters
        val sampleFieldsFactory = sampleInstanceFactory.withType(nestedType, nestedType)
        parameters.forEachIndexed { index, parameter ->
            buffer.addText("${parameter.name} = ")
            sampleFieldsFactory.addValue(
                klass = kTypeToKClass(parameter.type),
                buffer = buffer,
                isLast = (index == parameters.size - 1)
            )
        }
    }
}

enum class PrimaryConstructorFieldsSampleVisitorType {
    VALUE, `SYSTEM-TO-TEST`
}
