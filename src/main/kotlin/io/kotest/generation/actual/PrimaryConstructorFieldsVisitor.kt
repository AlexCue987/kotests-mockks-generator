package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.common.isDataClass
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.primaryConstructor

class PrimaryConstructorFieldsVisitor(
    private val actualInstanceFactory: ActualInstanceFactory,
    private val strategy: SerializeByFieldsStrategy = SerializeToInstanceStrategy()
)
    : SerializerVisitor {
    override fun canHandle(instance: Any?): Boolean = instance?.let {
        constructorToUse(instance) != null
    } ?: false

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        val klass: KClass<*> = instance!!::class
//        buffer.addLine("${klass.simpleName}(")
        if(strategy.canHandleInstance(klass, buffer)) {
            initalizeFields(instance, buffer)
//        buffer.addLine(")", isLast)
            strategy.afterInstance(klass, buffer, isLast)
            buffer.addClassName(klass.qualifiedName!!)
        }
    }

    fun constructorToUse(instance: Any): KFunction<Any>? {
        val primaryConstructor = instance::class.primaryConstructor
        if(primaryConstructor != null && primaryConstructor.visibility == KVisibility.PUBLIC) {
            return primaryConstructor
        }
        val anyPublicConstructor = instance::class.constructors.firstOrNull { it.visibility == KVisibility.PUBLIC }
        return anyPublicConstructor
    }

    private fun initalizeFields(instance: Any, buffer: CodeSnippet) {
        val fieldValues = FieldValueReader().constructorParameters(
            constructorToUse(instance)!!,
            strategy.exposePrivateFields,
            instance
        )
        fieldValues.forEachIndexed { index, fieldValue ->
//            buffer.addText("${fieldValue.name} = ")
            if(strategy.canHandleField(fieldValue.klass, fieldValue.name, fieldValue.value, buffer)
//                && strategy.outputValues
            ) {
                val isLast = (index == fieldValues.size - 1) || strategy.suppressCommas
                if(strategy.outputValues) {
                    actualInstanceFactory.innerLevelFactory.addValue(
                        instance = fieldValue.value,
                        buffer = buffer,
                        isLast = isLast
                    )
                } else {
                    buffer.addLine("", isLast)
                }
            }
        }
    }
}

interface SerializeByFieldsStrategy {
    fun canHandleInstance(klass: KClass<*>, buffer: CodeSnippet): Boolean
    fun afterInstance(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean)
    fun canHandleField(klass: KClass<*>, name: String, value: Any?, buffer: CodeSnippet): Boolean
    fun canHandleFieldValue(fieldValue: FieldValue, buffer: CodeSnippet): Boolean
    val suppressCommas: Boolean
    val exposePrivateFields: Boolean
    val outputValues: Boolean
}

class SerializeToInstanceStrategy(val suffix: String = ""): SerializeByFieldsStrategy {
    override fun canHandleInstance(klass: KClass<*>, buffer: CodeSnippet): Boolean {
        buffer.addLine("${klass.simpleName}(")
        return true
    }

    override fun afterInstance(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addLine(")$suffix", isLast)
    }

    override fun canHandleField(klass: KClass<*>, name: String, value: Any?, buffer: CodeSnippet): Boolean {
        buffer.addText("$name = ")
        return true
    }

    override fun canHandleFieldValue(fieldValue: FieldValue, buffer: CodeSnippet): Boolean {
        buffer.addText("${fieldValue.name} = ")
        return true
    }

    override val suppressCommas = false
    override val exposePrivateFields = true
    override val outputValues = true
}

class SerializeToAssertionOrMockStrategy(
    override val suppressCommas: Boolean,
    override val outputValues: Boolean,
    val fieldNameAndTypeSerializer: (name: String, type: String) -> String): SerializeByFieldsStrategy {
    override fun canHandleInstance(klass: KClass<*>, buffer: CodeSnippet): Boolean {
        return when {
            isDataClass(klass) -> {
                buffer.addClassName(klass.qualifiedName!!)
                true
            }
            else -> {
                buffer.addLine("// only data classes can be converted to assertions or mocks, cannot handle ${klass.qualifiedName}")
                false
            }
        }
    }

    override fun afterInstance(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {}

    override fun canHandleField(klass: KClass<*>, name: String, value: Any?, buffer: CodeSnippet): Boolean {
        buffer.addText(fieldNameAndTypeSerializer(name, klass.simpleName!!))
        return true
    }

    override fun canHandleFieldValue(fieldValue: FieldValue, buffer: CodeSnippet): Boolean {
        buffer.addText(fieldNameAndTypeSerializer(fieldValue.name, fieldValue.klass.simpleName!!))
        return true
    }

    override val exposePrivateFields = false
}

val SerializeToAssertionStrategy = SerializeToAssertionOrMockStrategy(suppressCommas = true, outputValues = true) {
    name, _ -> "actual.$name shouldBe "
}

val SerializeToMockValuesStrategy = SerializeToAssertionOrMockStrategy(suppressCommas = true, outputValues = true) {
        name, _ -> "every { ret.$name } returns "
}

val SerializeToMockFieldNamesStrategy = SerializeToAssertionOrMockStrategy(suppressCommas = true, outputValues = false) {
        name, _ -> "every { ret.$name } returns $name"
}

val SerializeToParameterizedMockStrategy = SerializeToAssertionOrMockStrategy(suppressCommas = false, outputValues = true) {
        name, type -> "$name: $type = "
}
