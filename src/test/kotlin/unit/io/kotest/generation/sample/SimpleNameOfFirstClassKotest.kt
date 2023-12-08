package io.kotest.generation.sample

import io.kotest.generation.generators.sample.simpleNameOfFirstClass
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDate

class SimpleNameOfFirstClassKotest: StringSpec() {
    init {
        "simpleNameOfFirstClass handles no params" {
            simpleNameOfFirstClass() shouldBe "NoArguments"
        }

        "simpleNameOfFirstClass handles one param" {
            simpleNameOfFirstClass(LocalDate::class) shouldBe "LocalDate"
        }

        "simpleNameOfFirstClass handles several params" {
            simpleNameOfFirstClass(BigDecimal::class, LocalDate::class) shouldBe "BigDecimal"
        }
    }
}