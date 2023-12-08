package io.kotest.generation.common

import io.kotest.generation.Fruit
import io.kotest.core.spec.style.StringSpec
import io.kotest.generation.WithAllBasicTypes
import io.kotest.generation.WithAllTemporaryTypes
import io.kotest.matchers.shouldBe

class SampleValueKotest: StringSpec() {
    private val factory = CodeSnippetFactory()

    init {
        "sampleInstance works for WithAllBasicTypes" {
            val actual = sampleValue(WithAllBasicTypes::class, factory)
            actual.sourceCode() shouldBe listOf(
                "WithAllBasicTypes(",
                "byte = 42,",
                "short = 42,",
                "int = 42,",
                "long = 42L,",
                "float = 42.0f,",
                "double = 42.0,",
                "uByte = 42u,",
                "uShort = 42u,",
                "uInt = 42u,",
                "uLong = 42uL,",
                "boolean = true,",
                "char = 'c',",
                "string = \"Whatever\"",
                ")"
                )
        }

        "sampleInstance works for WithAllDates" {
            val actual = sampleValue(WithAllTemporaryTypes::class, factory)
            actual.sourceCode() shouldBe listOf(
                "WithAllTemporaryTypes(",
                "dayOfWeek = DayOfWeek.MONDAY,",
                "instant = Instant.MIN,",
                "localDate = LocalDate.of(2021, 10, 25),",
                "localTime = LocalTime.of(10, 25),",
                "localDateTime = LocalDateTime.of(2021, 10, 25, 12, 34, 56),",
                "offsetDateTime = OffsetDateTime.of(2021, 12, 2, 1, 3, 3, 0, ZoneOffset.UTC),",
                "offsetTime = OffsetTime.of(1, 2, 3, 4, ZoneOffset.UTC),",
                "zoneId = ZoneId.of(\"America/Chicago\"),",
                "zoneOffset = ZoneOffset.UTC,",
                "zonedDateTime = ZonedDateTime.of(2021, 12, 1, 2, 3, 4, 5, ZoneOffset.UTC)",
                ")"
            )
        }

        "works with enums" {
            val actual = sampleValue(Fruit::class, factory)
            actual.sourceCode() shouldBe listOf(
                "Fruit(",
                "name = \"Whatever\",",
                "weight = BigDecimal(\"42\"),",
                "bestBefore = LocalDateTime.of(2021, 10, 25, 12, 34, 56),",
                "type = FruitType.FRESH",
                ")"
            )
        }
    }
}