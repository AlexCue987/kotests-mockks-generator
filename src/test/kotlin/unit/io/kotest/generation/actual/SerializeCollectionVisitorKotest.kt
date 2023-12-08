package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class SerializeCollectionVisitorKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    private val sut = SerializeCollectionVisitor(ActualInstanceFactory())
    private val buffer = CodeSnippet()

    init {
        "canHandle true for list" {
            sut.canHandle(listOf(1)) shouldBe true
        }

        "canHandle true for set" {
            sut.canHandle(setOf(1)) shouldBe true
        }

        "canHandle false for non-lists or sets" {
            sut.canHandle(mapOf(1 to "one")) shouldBe false
            sut.canHandle(null) shouldBe false
            sut.canHandle("not a list") shouldBe false
        }

        "handle empty list, last parameter" {
            sut.handle(listOf<String>(), buffer, true)
            buffer.sourceCode() shouldBe listOf("listOf()")
        }

        "handle empty list, not last parameter" {
            sut.handle(listOf<String>(), buffer, false)
            buffer.sourceCode() shouldBe listOf("listOf(),")
        }

        "handle empty set, not last parameter" {
            sut.handle(setOf<String>(), buffer, false)
            buffer.sourceCode() shouldBe listOf("setOf(),")
        }

        "handle non-empty list" {
            sut.handle(listOf("orange", LocalDate.of(2021, 12, 25)), buffer, true)
            buffer.sourceCode() shouldBe listOf(
                "listOf(",
                "\"\"\"orange\"\"\",",
                "LocalDate.of(2021, 12, 25)",
                ")"
            )
            buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf(
                "kotlin.String", "java.time.LocalDate"
            )
        }

        "handle non-empty set" {
            sut.handle(setOf("orange", LocalDate.of(2021, 12, 25)), buffer, true)
            buffer.sourceCode() shouldBe listOf(
                "setOf(",
                "\"\"\"orange\"\"\",",
                "LocalDate.of(2021, 12, 25)",
                ")"
            )
            buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf(
                "kotlin.String", "java.time.LocalDate"
            )
        }

    }
}