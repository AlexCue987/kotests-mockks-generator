package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.DayOfWeek

class EnumSampleValueVisitorKotest: StringSpec() {
    private val sut = EnumSampleValueVisitor()

    init {
        "canHandle true for enum" {
            sut.canHandle(DayOfWeek::class) shouldBe true
        }

        "canHandle false for other class" {
            sut.canHandle(String::class) shouldBe false
        }

        "handle works" {
            val buffer = CodeSnippet()
            sut.handle(DayOfWeek::class, buffer, true)
            buffer.qualifiedNames() shouldBe listOf("java.time.DayOfWeek")
            buffer.sourceCode() shouldBe listOf("DayOfWeek.MONDAY")
        }
    }
}