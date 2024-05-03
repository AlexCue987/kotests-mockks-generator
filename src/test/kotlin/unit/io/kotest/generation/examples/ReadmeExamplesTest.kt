package io.kotest.generation.examples

import io.kotest.core.spec.style.StringSpec
import io.kotest.generation.generators.actual.serializeToAssertions
import io.kotest.generation.generators.actual.serializeToKotlin
import io.kotest.generation.generators.actual.serializeToMocks
import io.kotest.generation.generators.sample.generateSampleInstances
import io.kotest.generation.serializeForTests
import java.math.BigDecimal

class ReadmeExamplesTest: StringSpec() {
    private val thing = SampleComplexThing(
        name = "Whatever",
        box = SampleBox(
            length = BigDecimal("3.2"),
            width = BigDecimal("2.1"),
            height = BigDecimal("1")
        ),
        orderedItems = listOf(
            SampleItem(
                name = "Apple",
                weight = BigDecimal("1.2")
            ),
            SampleItem(
                name = "Orange",
                weight = BigDecimal("2.3")
            ),
        ),
        prioritizedItems = mapOf(
            SampleItem(
                name = "Banana",
                weight = BigDecimal("1.3")
            ) to 1
        )
    )

    private val box = SampleBox(
        length = BigDecimal("3.4"),
        width = BigDecimal("2.3"),
        height = BigDecimal("1.2")
    )


    init {
        "generate assignments, assertions, and mocks".config(enabled = false) {
            serializeForTests(thing, box)
        }
        "sample".config(enabled = false) {
            generateSampleInstances(
                SampleComplexThing::class,
                SampleItem::class
            )
        }
    }

    data class SampleComplexThing(
        val name: String,
        val box: SampleBox,
        val orderedItems: List<SampleItem>,
        val prioritizedItems: Map<SampleItem, Int>
    )

    data class SampleBox(
        val length: BigDecimal,
        val width: BigDecimal,
        val height: BigDecimal
    )

    data class SampleItem(
        val name: String,
        val weight: BigDecimal
    )
}