package io.kotest.generation.actual

import io.kotest.generation.generators.actual.simpleNameOfFirstInstance
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime

class SimpleNameOfFirstInstanceKotest: StringSpec() {
    init {
        "simpleNameOfFirstInstance handles no args" {
            simpleNameOfFirstInstance() shouldBe "NoArguments"
        }

        "simpleNameOfFirstInstance handles one arg" {
            simpleNameOfFirstInstance(LocalDate.of(2023, 4, 10)) shouldBe "LocalDate"
        }

        "simpleNameOfFirstInstance handles several args" {
            simpleNameOfFirstInstance("Some String", LocalDate.of(2023, 4, 10)) shouldBe "String"
        }
    }
}