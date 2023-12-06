package com.tgt.trans.dmo.common.examples

import com.tgt.trans.dmo.common.generation.*
import com.tgt.trans.dmo.common.generation.actual.ActualInstanceFactory
import com.tgt.trans.dmo.common.generation.actual.ExactClassSerializer
import com.tgt.trans.dmo.common.generation.actual.SampleData
import com.tgt.trans.dmo.common.generation.generators.kotest.generateAllTests
import io.kotest.core.spec.style.StringSpec
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class UnitTestsGeneratorKotest: StringSpec() {
    init {
        "basic example".config(enabled = false) {
            generateAllTests(
                Box::class,
                "src/test/kotlin/generated/BoxKotest.kt",
            )
        }

        "huge example".config(enabled = false) {
            generateAllTests(
                AnotherService::class,
                "src/test/kotlin/generated/AnotherServiceKotest.kt",
            )
        }
    }
}