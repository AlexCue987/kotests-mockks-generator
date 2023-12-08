package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*
import kotlin.reflect.KClass

class DefaultSampleValueVisitor: SampleSerializerVisitor {
    private val visitors = defaultSampleValues.entries.associateBy(
        { it.key },
        { ExactClassSampleValueVisitor(it.key, it.value) }
    )

    override fun canHandle(klass: KClass<*>): Boolean = klass in defaultSampleValues.keys

    override fun handle(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        visitors[klass]?.serialize(klass, buffer, isLast)
    }

    fun supportedClasses() = visitors.mapNotNull { it.key.qualifiedName }.sorted()

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
    }
}

