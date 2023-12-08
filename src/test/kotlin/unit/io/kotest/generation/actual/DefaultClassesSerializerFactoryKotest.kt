package io.kotest.generation.actual

import io.kotest.generation.Fruit
import io.kotest.generation.FruitType
import io.kotest.generation.common.CodeSnippet
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*

class DefaultClassesSerializerFactoryKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    private val sut =  DefaultClassesSerializerFactory.defaultClassesSerializer()

    private val buffer = CodeSnippet()

    init {

        "serialize basic types" {
            forAll(
                row(22.toByte(), "fieldName = 22,", "kotlin.Byte"),
                row(22.toShort(), "fieldName = 22,", "kotlin.Short"),
                row(22, "fieldName = 22,", "kotlin.Int"),
                row(22L, "fieldName = 22L,", "kotlin.Long"),
                row(22f, "fieldName = 22.0f,", "kotlin.Float"),

                row(22.0, "fieldName = 22.0,", "kotlin.Double"),
                row(22.toUByte(), "fieldName = 22.toUByte(),", "kotlin.UByte"),
                row(22.toUShort(), "fieldName = 22.toUShort(),", "kotlin.UShort"),
                row(22.toUInt(), "fieldName = 22u,", "kotlin.UInt"),
                row(true, "fieldName = true,", "kotlin.Boolean"),
                row('c', "fieldName = 'c',", "kotlin.Char"),
            ) {
                    instance, expectedValue, expectedType ->
                val buffer = CodeSnippet()
                buffer.addText("fieldName = ")
                sut.serialize(instance, buffer, false)
                withClue(expectedType) {
                    assertSoftly {
                        buffer.sourceCode() shouldBe listOf(expectedValue)
                        buffer.qualifiedNames() shouldBe listOf(expectedType)
                    }
                }
            }
        }

        "serialize types in frequently used libraries" {
            forAll(
                row(BigDecimal("12.34"), "fieldName = BigDecimal(\"12.34\"),", "java.math.BigDecimal"),
                row(UUID.fromString("b93e783d-8382-480d-ab5f-a0fc8e53006f"), "fieldName = UUID.fromString(\"b93e783d-8382-480d-ab5f-a0fc8e53006f\"),", "java.util.UUID"),
                row(BigInteger.valueOf(15L), "fieldName = BigInteger.valueOf(15L),", "java.math.BigInteger"),
                row("Some String", "fieldName = \"\"\"Some String\"\"\",", "kotlin.String"),
                row(Instant.ofEpochMilli(12345678901L), "fieldName = Instant.ofEpochMilli(12345678901L),", "java.time.Instant"),

                row(LocalDate.of(2021, 12, 8), "fieldName = LocalDate.of(2021, 12, 8),", "java.time.LocalDate"),
                row(LocalTime.of(12, 11, 10, 9), "fieldName = LocalTime.of(12, 11, 10, 9),", "java.time.LocalTime"),
                row(LocalDateTime.of(2021, 12, 8, 12, 11, 10, 9), "fieldName = LocalDateTime.of(2021, 12, 8, 12, 11, 10, 9),", "java.time.LocalDateTime"),
                row(OffsetDateTime.of(2021, 12, 8, 12, 11, 10, 9, ZoneOffset.of("-07:00")), "fieldName = OffsetDateTime.of(2021, 12, 8, 12, 11, 10, 9, ZoneOffset.of(\"-07:00\")),", "java.time.OffsetDateTime"),
                row(OffsetTime.of(12, 11, 10, 9, ZoneOffset.of("-07:00")), "fieldName = OffsetTime.of(12, 11, 10, 9, ZoneOffset.of(\"-07:00\")),", "java.time.OffsetTime"),

                row(ZoneId.of("America/Chicago"), "fieldName = ZoneId.of(\"America/Chicago\"),", "java.time.ZoneId"),
                row(ZoneOffset.of("-03:30"), "fieldName = ZoneId.of(\"-03:30\"),", "java.time.ZoneId"),
                row(ZonedDateTime.of(2021, 12, 8, 12, 11, 10, 9, ZoneOffset.of("-04:15")), "fieldName = ZonedDateTime.of(2021, 12, 8, 12, 11, 10, 9, ZoneOffset.of(\"-04:15\")),", "java.time.ZonedDateTime"),
            ) {
                    instance, expectedValue, expectedType ->
                val buffer = CodeSnippet()
                buffer.addText("fieldName = ")
                sut.serialize(instance, buffer, false)
                withClue(expectedType) {
                    assertSoftly {
                        buffer.sourceCode() shouldBe listOf(expectedValue)
                        buffer.qualifiedNames() shouldBe listOf(expectedType)
                    }
                }
            }
        }

        "does not serialize non-supported type" {
            val instance = Fruit("orange", BigDecimal.ONE, LocalDateTime.now(), FruitType.FRESH)
            val buffer = CodeSnippet()
            sut.serialize(instance, buffer, false) shouldBe false
            buffer.qualifiedNames() shouldBe listOf()
            buffer.sourceCode() shouldBe listOf()
        }

        "supportedClasses" {
            (LocalDate::class in DefaultClassesSerializerFactory.supportedClasses) shouldBe true
            (Fruit::class in DefaultClassesSerializerFactory.supportedClasses) shouldBe false
        }
    }
}