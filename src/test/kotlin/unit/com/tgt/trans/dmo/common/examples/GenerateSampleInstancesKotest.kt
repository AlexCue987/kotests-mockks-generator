package com.tgt.trans.dmo.common.examples

import com.tgt.trans.dmo.common.generation.*
import com.tgt.trans.dmo.common.generation.common.CodeSnippetFactory
import com.tgt.trans.dmo.common.generation.generators.sample.generateSampleInstances
import io.kotest.core.spec.style.StringSpec
import java.math.BigDecimal

class GenerateSampleInstancesKotest: StringSpec() {
    init {
        "generate instances".config(enabled = false) {
            generateSampleInstances(
                "src/test/kotlin/generated/SampleData.kt",
                MyThing::class,
                NestedThing::class,
                ComplexThing::class,
                MyThingWithPrivateWeight::class,
                Fruit::class
            )
        }
    }
}