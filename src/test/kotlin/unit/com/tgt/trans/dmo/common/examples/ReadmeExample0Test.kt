package com.tgt.trans.dmo.common.examples

import com.tgt.trans.dmo.common.generation.generators.actual.serializeToAssertions
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToKotlin
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToMocks
import com.tgt.trans.dmo.common.generation.generators.sample.generateSampleInstances
import io.kotest.core.spec.style.StringSpec
import java.math.BigDecimal

class ReadmeExample0Test: StringSpec() {
    init {
        "serialize actual nested object".config(enabled = false) {
            val params = Box(
                length = BigDecimal("3"),
                width = BigDecimal("2"),
                height = BigDecimal("1")
            )
            val results = SampleComplexClass(
                name = "some name",
                box = Box(
                    length = BigDecimal.ONE,
                    width = BigDecimal("2"),
                    height = BigDecimal("3")
                ),
                orderedItems = listOf(Item("apple", 1)),
                prioritizedItems = mapOf(
                    Item("apple", 1) to 1,
                    Item("orange", 1) to 2,
                )
            )
            serializeToKotlin(params, results)
            serializeToKotlin("ActualInstance.kt", params, results)
            serializeToAssertions(params, results)
            serializeToAssertions("ActualInstanceAssertions.kt", params, results)
            serializeToMocks(params, results)
            serializeToMocks("ActualInstanceMocks.kt", params, results)
        }

        "generate sample data".config(enabled = false) {
            generateSampleInstances(
                "ActualInstance.kt",
                SampleComplexClass::class,
                Item::class
            )

            generateSampleInstances(
                SampleComplexClass::class,
                Item::class
            )
        }
    }

    data class Box(
        val length: BigDecimal,
        val width: BigDecimal,
        val height: BigDecimal
    )

    data class Item(
        val name: String,
        val quantity: Int
    )

    data class SampleComplexClass(
        val name: String,
        val box: Box,
        val orderedItems: List<Item>,
        val prioritizedItems: Map<Item, Int>
    )
}