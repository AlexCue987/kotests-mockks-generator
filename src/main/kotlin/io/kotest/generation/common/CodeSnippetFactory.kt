package io.kotest.generation.common

import io.kotest.generation.kotest.NameValueFormatter
import io.kotest.generation.sample.LastResortSimpleMockVisitor
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

class CodeSnippetFactory(private val sampleValues: Map<KClass<*>, () -> String> = defaultSampleValues) :
    ParametersInitializer {
    private val classesStack = ArrayDeque<KClass<*>>(listOf())

    val supportedClasses by lazy { sampleValues.keys }

    fun sampleValueForSupportedClass(klass: KClass<*>) = sampleValues[klass]?.let { it() }

    fun addParameter(
        parameter: KParameter,
        buffer: CodeSnippet,
        isLast: Boolean,
        nameValueFormatter: NameValueFormatter
    ) {
        val klass = kParameterToKClass(parameter)
        val parameterName = parameter.name!!
        addValue(klass, buffer, isLast, nameValueFormatter, parameterName)
    }

    fun addValue(
        klass: KClass<*>,
        buffer: CodeSnippet,
        isLast: Boolean,
        nameValueFormatter: NameValueFormatter,
        parameterName: String
    ) {
        if(klass in classesStack) {
            buffer.addLine("// not serializing field of type ${klass.qualifiedName} again, to avoid infinite loop")
            return
        }
        try {
            classesStack.add(klass)
            when {
                klass in sampleValues -> addValue(
                    klass,
                    sampleValues[klass]!!(),
                    buffer,
                    isLast,
                    nameValueFormatter,
                    parameterName
                )
                isEnum(klass) -> addValue(
                    klass,
                    "${klass.simpleName}.${firstEnumValue(klass)}",
                    buffer,
                    isLast,
                    nameValueFormatter,
                    parameterName
                )
                List::class.java.isAssignableFrom(klass.java) -> addValue(
                    klass,
                    "listOf()",
                    buffer,
                    isLast,
                    nameValueFormatter,
                    parameterName
                )
                Set::class.java.isAssignableFrom(klass.java) -> addValue(
                    klass,
                    "setOf()",
                    buffer,
                    isLast,
                    nameValueFormatter,
                    parameterName
                )
                Map::class.java.isAssignableFrom(klass.java) -> addValue(
                    klass,
                    "mapOf()",
                    buffer,
                    isLast,
                    nameValueFormatter,
                    parameterName
                )
                klass.isData -> {
                    val nestedParameter = sampleInstance(klass, this)
                    buffer.addClassNames(nestedParameter.qualifiedNames())
                    val nestedParameterCode = nestedParameter.sourceCodeAsOneString()
                    buffer.addLine(nameValueFormatter(parameterName, nestedParameterCode, isLast))
                }
                //TODO: should we customize fallback?
                else -> addLastResortMockk(
                    klass,
                    buffer,
                    isLast,
                    nameValueFormatter,
                    parameterName
                )
            }
        } finally {
            classesStack.removeLastOrNull()
        }
    }

    private fun addLastResortMockk(
        klass: KClass<*>,
        buffer: CodeSnippet,
        isLast: Boolean,
        nameValueFormatter: NameValueFormatter,
        parameterName: String
    ) {
        val bufferForMockk = CodeSnippet()
        LastResortSimpleMockVisitor(this).handle(klass, bufferForMockk, isLast = true)
        val value = bufferForMockk.sourceCodeAsOneString()
        val line = nameValueFormatter(parameterName, value, isLast)
        buffer.addClassNames(bufferForMockk.qualifiedNames())
        buffer.addLine(line)
    }

    private fun addValue(
        klass: KClass<*>,
        value: String,
        buffer: CodeSnippet,
        isLast: Boolean,
        nameValueFormatter: NameValueFormatter,
        name: String
    ) {
        buffer.addClassName(klass.qualifiedName!!)
        val line = nameValueFormatter(name, value, isLast)
        buffer.addLine(line)
    }

    override fun initializeParameters(
        parameters: List<KParameter>,
        ret: CodeSnippet,
        nameValueFormatter: NameValueFormatter
    ) {
        parameters.forEachIndexed { index, parameter ->
            val lastParameter = (index == parameters.size - 1)
            addParameter(parameter, ret, lastParameter, nameValueFormatter)
        }
    }

    companion object {
        private val defaultSampleValues = mapOf<KClass<*>, () -> String>(
            Byte::class to { "42" },
            Short::class to { "42" },
            Int::class to { "42" },
            Long::class to { "42L" },
            Float::class to { "42.0f" },
            Double::class to { "42.0" },
            UByte::class to { "42u" },
            UShort::class to { "42u" },
            UInt::class to { "42u" },
            ULong::class to { "42uL" },
            Boolean::class to { "true" },
            Char::class to { "'c'" },
            BigDecimal::class to { "BigDecimal(\"42\")" },
            UUID::class to { "${UUID.randomUUID()}" },
            BigInteger::class to {"BigInteger.valueOf(42L)" },
            String::class to { "\"Whatever\"" },
            Instant::class to { "Instant.MIN" },
            LocalDate::class to { "LocalDate.of(2021, 10, 25)" },
            LocalTime::class to { "LocalTime.of(10, 25)" },
            LocalDateTime::class to { "LocalDateTime.of(2021, 10, 25, 12, 34, 56)" },
            OffsetDateTime::class to { "OffsetDateTime.of(2021, 12, 2, 1, 3, 3, 0, ZoneOffset.UTC)" },
            OffsetTime::class to { "OffsetTime.of(1, 2, 3, 4, ZoneOffset.UTC)" },
            ZoneId::class to { "ZoneId.of(\"America/Chicago\")" },
            ZoneOffset::class to { "ZoneOffset.UTC" },
            ZonedDateTime::class to { "ZonedDateTime.of(2021, 12, 1, 2, 3, 4, 5, ZoneOffset.UTC)" }
        )

        fun withAdditionalSampleValues(vararg moreSampleValues: Pair<KClass<*>, () -> String>) =
            CodeSnippetFactory(defaultSampleValues + moreSampleValues)
    }
}
