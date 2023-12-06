package com.tgt.trans.dmo.common.generation.actual

import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class NullValueVisitorKotest:StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    private val sut = NullValueVisitor()
    private val buffer = CodeSnippet()

    init {
        "handle null last line" {
            sut.serialize(null, buffer, true) shouldBe true
            buffer.sourceCode() shouldBe listOf("null")
        }

        "handle null not last line" {
            sut.serialize(null, buffer, false) shouldBe true
            buffer.sourceCode() shouldBe listOf("null,")
        }

        "handle not null not" {
            sut.serialize("NotNull", buffer, false) shouldBe false
            buffer.sourceCode().isEmpty() shouldBe true
        }
    }
}