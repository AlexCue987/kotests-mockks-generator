package io.kotest.generation.actual

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StringToCodeKotest: StringSpec() {
    init {
        "handle single line value" {
            val value = "value on one line"
            DefaultClassesSerializerFactory.stringToCode(value) shouldBe "\"\"\"$value\"\"\""
        }

        "handle multiple line value" {
            val value = """value
on
multiple
lines""".trimMargin()
            val tripleDoubleQuotes = "\"\"\""
            DefaultClassesSerializerFactory.stringToCode(value) shouldBe
                    tripleDoubleQuotes +
                    """value
on
multiple
lines""" + tripleDoubleQuotes
        }
    }
}