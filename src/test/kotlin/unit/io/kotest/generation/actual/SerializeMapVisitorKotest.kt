package io.kotest.generation.actual

import io.kotest.generation.Fruit
import io.kotest.generation.MyThing
import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class SerializeMapVisitorKotest: StringSpec() {
    private val sut = SerializeMapVisitor(ActualInstanceFactory())

    init {
        "handle empty map, last parameter" {
            val buffer =  CodeSnippet()
            sut.serialize(mapOf<Any, Any>(), buffer, isLast = true)
            buffer.qualifiedNames().isEmpty() shouldBe true
            buffer.sourceCode() shouldBe listOf("mapOf()")
        }

        "handle empty map, not last parameter" {
            val buffer =  CodeSnippet()
            sut.serialize(mapOf<Any, Any>(), buffer, isLast = false)
            buffer.qualifiedNames().isEmpty() shouldBe true
            buffer.sourceCode() shouldBe listOf("mapOf(),")
        }

        "handle map of basic keys and types" {
            val buffer =  CodeSnippet()
            sut.serialize(mapOf<String, LocalDate>(
                "today" to LocalDate.of(2021, 12, 24),
                "tomorrow" to LocalDate.of(2021, 12, 25),
            ), buffer, isLast = true)
            buffer.qualifiedNames() shouldBe listOf(
                "kotlin.String",
                "java.time.LocalDate",
                )
            buffer.sourceCode() shouldBe listOf(
                "mapOf(",
                "\"\"\"today\"\"\"",
                "to",
                "LocalDate.of(2021, 12, 24),",
                "\"\"\"tomorrow\"\"\"",
                "to",
                "LocalDate.of(2021, 12, 25)",
                ")")
        }

        "handle map when keys and types are data classes" {
            val buffer =  CodeSnippet()
            sut.serialize(mapOf<MyThing, Fruit>(
                SampleData.myThing() to SampleData.fruit()
            ), buffer, isLast = true)
            buffer.qualifiedNames() shouldContainExactlyInAnyOrder  listOf(
                "io.kotest.generation.MyThing",
                "kotlin.String",
                "java.math.BigDecimal",
                "io.kotest.generation.Fruit",
                "java.time.LocalDateTime",
                "io.kotest.generation.FruitType",
            )
//            buffer.sourceCode().forEach {
//                println("\"\"\"$it\"\"\",")
//            }

            buffer.sourceCode() shouldBe listOf(
                """mapOf(""",
                """MyThing(""",
                "name = \"\"\"Sample String\"\"\",",
                """weight = BigDecimal("41")""",
                """)""",
                """to""",
                """Fruit(""",
                "name = \"\"\"Sample String\"\"\",",
                """weight = BigDecimal("41"),""",
                """bestBefore = LocalDateTime.of(2021, 10, 25, 12, 34, 56, 0),""",
                """type = FruitType.FRESH""",
                """)""",
                """)""",
            )
        }

    }
}