package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ExactClassSampleValueVisitorKotest: StringSpec() {
    private val sut = ExactClassSampleValueVisitor(Int::class) { "42" }

    init {
        "canHandle true for same class" {
            sut.canHandle(Int::class) shouldBe true
        }

        "canHandle false for other class" {
            sut.canHandle(Byte::class) shouldBe false
        }

        "handle works" {
            val buffer = CodeSnippet()
            sut.handle(Int::class, buffer, true)
            buffer.qualifiedNames() shouldBe listOf("kotlin.Int")
            buffer.sourceCode() shouldBe listOf("42")
        }
    }
}