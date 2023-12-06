package com.tgt.trans.dmo.common.examples

import com.tgt.trans.dmo.common.generation.ThingFactory
import com.tgt.trans.dmo.common.generation.generators.kotest.generateAllTests
import com.tgt.trans.dmo.common.generation.generators.kotest.generateAllTestsAndMockks
import com.tgt.trans.dmo.common.generation.mockk.generateAllMockks
import io.kotest.core.spec.style.StringSpec

class ReadmeExample1Test: StringSpec() {
    init {
        "generate all tests and mocks for ThingFactory".config(enabled = false) {
            generateAllTestsAndMockks(
                ThingFactory::class
            )
            generateAllTestsAndMockks(
                ThingFactory::class,
                folder = "src/test/kotlin/generated",
            )
        }

        "generate all tests for ThingFactory".config(enabled = false) {
            generateAllTests(
                ThingFactory::class,
                fileName = "src/test/kotlin/generated/ThingFactoryTest.kt",
            )
        }

        "generate all mocks for ThingFactory".config(enabled = false) {
            generateAllMockks(
                ThingFactory::class,
                fileName = "src/test/kotlin/generated/MockkThingFactory.kt",
            )
        }
    }
}