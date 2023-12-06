package com.tgt.trans.dmo.common.generation.actual

import com.tgt.trans.dmo.common.generation.ComplexThing
import com.tgt.trans.dmo.common.generation.Fruit
import com.tgt.trans.dmo.common.generation.FruitType
import com.tgt.trans.dmo.common.generation.MyThing
import com.tgt.trans.dmo.common.generation.MyThingWithPrivateWeight
import com.tgt.trans.dmo.common.generation.NestedThing
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set

// generated by com.tgt.trans.dmo.common:kotests-generator
class SampleData {
    companion object {
        fun myThing() = MyThing(
            name = "Sample String",
            weight = BigDecimal("41")
        )

        fun nestedThing() = NestedThing(
            name = "Sample String",
            weight = BigDecimal("41"),
            counter = BigInteger.valueOf(42L),
            createdAt = LocalDateTime.of(2021, 10, 25, 12, 34, 56),
            myThing = MyThing(
                name = "Sample String",
                weight = BigDecimal("41")
            )
        )

        fun complexThing() = ComplexThing(
            name = "Sample String",
            orderedItems = listOf(),
            distinctItems = setOf(),
            mappedThings = mapOf()
         )

        fun myThingWithPrivateWeight() = MyThingWithPrivateWeight(
            name = "Sample String",
            weight = BigDecimal("41")
        )

        fun fruit() = Fruit(
            name = "Sample String",
            weight = BigDecimal("41"),
            bestBefore = LocalDateTime.of(2021, 10, 25, 12, 34, 56),
            type = FruitType.FRESH
        )
    }
}