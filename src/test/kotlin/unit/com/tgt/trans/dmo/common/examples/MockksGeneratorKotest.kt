package com.tgt.trans.dmo.common.examples

import com.tgt.trans.dmo.common.examples.saved.output.MockkBoxWithModifiedFields
import com.tgt.trans.dmo.common.generation.*
import com.tgt.trans.dmo.common.generation.mockk.generateAllMockks
import com.tgt.trans.dmo.common.generation.mockk.generateMockks
import io.kotest.core.spec.style.StringSpec

class MockksGeneratorKotest: StringSpec() {
    init {
        "basic example".config(enabled = false) {
            generateAllMockks(
                Box::class,
                "src/test/kotlin/generated/AllMockksForBox.kt",
            )
            generateAllMockks(
                Box::class,
                "src/test/kotlin/generated/SomeBoxMockks.kt",
            )
            generateAllMockks(
                AnotherService::class,
                "src/test/kotlin/generated/MockksForVoidMethods.kt",
            )
        }

        "use generated mockks" {
            val box = MockkBoxWithModifiedFields().get()
            listOf(
                Box::length,
                Box::width,
                Box::height,
                Box::volume,
                Box::flipOverLength
            ).forEach {
                println("${it.name} = ${it.call(box)}")
            }
        }
    }
}