package io.kotest.generation.kotest

import io.kotest.generation.MyThing
import io.kotest.generation.common.CodeSnippetFactory
import io.kotest.generation.common.sampleInstance
import io.kotest.generation.common.sampleValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek

class SampleInstanceKotest: StringSpec() {
    private val sut = CodeSnippetFactory()

    init {
        "handle data class" {
            val actual = sampleInstance(MyThing::class, sut)
            actual.sourceCode() shouldBe  listOf("MyThing(",
                "name = \"Whatever\",",
                "weight = BigDecimal(\"42\")",
                ")")
        }

        "handle collection" {
            forAll(
                row(List::class, "listOf()"),
                row(Map::class, "mapOf()"),
                row(Set::class, "setOf()")
            ) {
                klass, expected -> sampleValue(klass, sut).sourceCode() shouldBe listOf(expected)
            }
        }

        "handle enums" {
            sampleValue(DayOfWeek::class, sut).sourceCode() shouldBe listOf("DayOfWeek.MONDAY")
        }

        "handle some basic types" {
            forAll(
                row(Int::class, "42"),
                row(Char::class, "'c'"),
                row(Float::class, "42.0f")
            ) {
                    klass, expected -> sampleValue(klass, sut).sourceCode() shouldBe listOf(expected)
            }
        }
    }
}