package io.kotest.generation.sample

import io.kotest.generation.MyThing
import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class DefaultSampleValueVisitorKotest: StringSpec() {
    private val sut = DefaultSampleValueVisitor()

    init {
        "canHandle true" {
            sut.canHandle(BigDecimal::class) shouldBe true
        }

        "canHandle false" {
            sut.canHandle(MyThing::class) shouldBe false
        }

        "handle" {
            val buffer = CodeSnippet()
            sut.handle(Float::class, buffer, true)
            buffer.qualifiedNames() shouldBe listOf("kotlin.Float")
            buffer.sourceCode() shouldBe listOf("42.0f")
        }
    }
}