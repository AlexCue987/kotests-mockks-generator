package io.kotest.generation.actual

import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*
import kotlin.reflect.KClass

class DefaultClassesSerializerFactory {
    companion object {
        private val defaultSerializers = mapOf<KClass<*>, (Any) -> String>(
            Byte::class to { value: Any -> "$value" },
            Short::class to { value: Any -> "$value" },
            Int::class to { value: Any -> "$value" },
            Long::class to { value: Any -> "${value}L" },
            Float::class to { value: Any -> "${value}f" },
            Double::class to { value: Any -> "$value" },
            UByte::class to { value: Any -> "${value}.toUByte()" },
            UShort::class to { value: Any -> "${value}.toUShort()" },
            UInt::class to { value: Any -> "${value}u" },
            ULong::class to { value: Any -> "${value}uL" },
            Boolean::class to { value: Any -> "$value" },
            Char::class to { value: Any -> "'${value}'" },
            BigDecimal::class to { value: Any -> "BigDecimal(\"$value\")" },
            UUID::class to { value: Any -> "UUID.fromString(\"${value}\")" },
            BigInteger::class to { value: Any -> "BigInteger.valueOf(${value}L)" },
            String::class to { value: Any -> stringToCode(value) },
            Instant::class to { value: Any -> "Instant.ofEpochMilli(${(value as Instant).toEpochMilli()}L)" },
            LocalDate::class to { value: Any ->
                with(value as LocalDate) {
                    "LocalDate.of(${year}, ${monthValue}, ${dayOfMonth})"
                }
            },
            LocalTime::class to { value: Any ->
                with(value as LocalTime) {
                    "LocalTime.of(${hour}, ${minute}, ${second}, ${nano})"
                }
            },
            LocalDateTime::class to { value: Any ->
                with(value as LocalDateTime) {
                    "LocalDateTime.of(${year}, ${monthValue}, ${dayOfMonth}, ${hour}, ${minute}, ${second}, ${nano})"
                }
            },
            OffsetDateTime::class to { value: Any ->
                with(value as OffsetDateTime) {
                    "OffsetDateTime.of(${year}, ${monthValue}, ${dayOfMonth}, ${hour}, ${minute}, ${second}, ${nano}, ZoneOffset.of(\"$offset\"))"
                }
            },
            OffsetTime::class to { value: Any ->
                with(value as OffsetTime) {
                    "OffsetTime.of(${hour}, ${minute}, ${second}, ${nano}, ZoneOffset.of(\"$offset\"))"
                }
            },
            ZonedDateTime::class to { value: Any ->
                with(value as ZonedDateTime) {
                    "ZonedDateTime.of(${year}, ${monthValue}, ${dayOfMonth}, ${hour}, ${minute}, ${second}, ${nano}, ZoneOffset.of(\"$offset\"))"
                }
            }
        )

        private val zoneIdSerialiser = DescendantOfClassSerializer(ZoneId::class) { value: Any ->
            with(value as ZoneId) {
                "ZoneId.of(\"$id\")"
            }
        }

        private fun allDefaultSerializers(): List<SerializerVisitor> {
            val ret = mutableListOf<SerializerVisitor>(zoneIdSerialiser)
            defaultSerializers.forEach { klass, serializer ->
                ret.add(ExactClassSerializer(klass, serializer))
            }
            return ret
        }

        fun stringToCode(value: Any): String = "\"\"\"${value.toString()}\"\"\""

        fun defaultClassesSerializer() = SerializersList(allDefaultSerializers())

        val supportedClasses by lazy { defaultSerializers.keys }
    }
}