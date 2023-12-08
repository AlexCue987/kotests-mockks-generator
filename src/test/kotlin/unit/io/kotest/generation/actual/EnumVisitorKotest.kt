package io.kotest.generation.actual

import io.kotest.generation.FruitType
import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EnumVisitorKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    private val sut = EnumVisitor()

    init {
        "serialize all enums" {
            FruitType.values().forEach { fruitType ->
                val buffer = CodeSnippet()
                sut.serialize(fruitType, buffer, true) shouldBe true
                buffer.qualifiedNames() shouldBe listOf("io.kotest.generation.FruitType")
                buffer.sourceCode() shouldBe listOf("FruitType.$fruitType")
            }
        }

        "end with comma if not lasst" {
            FruitType.values().forEach { fruitType ->
                val buffer = CodeSnippet()
                sut.serialize(fruitType, buffer, false) shouldBe true
                buffer.qualifiedNames() shouldBe listOf("io.kotest.generation.FruitType")
                buffer.sourceCode() shouldBe listOf("FruitType.$fruitType,")
            }
        }

        "do not serialize not an enum" {
            val buffer = CodeSnippet()
            sut.serialize("Something", buffer, true) shouldBe false
            buffer.qualifiedNames() shouldBe listOf()
            buffer.sourceCode() shouldBe listOf()
        }

        "wrap in back-ticks" {
            val buffer = CodeSnippet()
            sut.serialize(TicketType.`NON-REFUNDABLE`, buffer, true) shouldBe true
            buffer.sourceCode() shouldBe listOf("TicketType.`NON-REFUNDABLE`")
        }
    }

    private enum class TicketType { RETURN, `NON-REFUNDABLE` }
}