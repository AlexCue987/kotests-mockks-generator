package com.tgt.trans.dmo.common.examples

import com.tgt.trans.dmo.common.generation.*
import com.tgt.trans.dmo.common.generation.actual.ActualInstanceFactory
import com.tgt.trans.dmo.common.generation.actual.DefaultClassesSerializerFactory
import com.tgt.trans.dmo.common.generation.actual.ExactClassSerializer
import com.tgt.trans.dmo.common.generation.actual.SampleData
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToAssertions
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToKotlin
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToMocks
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToParameterizedMocks
import com.tgt.trans.dmo.common.generation.generators.kotest.generateAllKotests
import com.tgt.trans.dmo.common.generation.generators.kotest.generateAllTests
import com.tgt.trans.dmo.common.generation.generators.parameterized.generateParamsKotest
import com.tgt.trans.dmo.common.generation.mockk.generateAllMockks
import com.tgt.trans.dmo.common.generation.sample.DefaultSampleValueVisitor
import com.tgt.trans.dmo.common.generation.sample.ExactClassSampleValueVisitor
import com.tgt.trans.dmo.common.generation.sample.SampleInstanceFactory
import com.tgt.trans.dmo.common.generation.sample.generateSampleInstances
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ReadmeExamplesKotest: StringSpec() {
    init {
        "Example 0 - generate all tests for ThingFactory".config(enabled = false) {
            generateAllTests(
                ThingFactory::class,
                "src/test/kotlin/unit/generated/Example0.kt",
            )
        }

        "Example 1 - generate all mocks for ThingFactory".config(enabled = false) {
            generateAllMockks(
                ThingFactory::class,
                "src/test/kotlin/unit/generated/Example1.kt",
            )
        }

        "Example 2 - serialize actual instances into Kotlin objects".config(enabled = false) {
            val myThing = MyThing(
                name = "Sample String",
                weight = BigDecimal("123")
            )
            val myNestedThing = NestedThing(
                name = "Sample String",
                weight = BigDecimal("41"),
                counter = BigInteger.valueOf(42L),
                createdAt = LocalDateTime.of(2021, 10, 25, 12, 34, 56),
                myThing = MyThing(
                    name = "Sample String",
                    weight = BigDecimal("41")
                )
            )
            val myList = listOf(
                MyThingWithPrivateWeight(
                    name = "Another String",
                    weight = BigDecimal("321")
                )
            )
            serializeToKotlin(
                "src/test/kotlin/unit/generated/Example2a.kt",
                myThing,
                myList,
                myNestedThing,
                LocalDateTime.of(2021, 12, 28, 1,2, 3, 4)
            )
            serializeToMocks(
                "src/test/kotlin/unit/generated/Example2b.kt",
                myThing,
                myNestedThing
            )
            serializeToAssertions(
                "src/test/kotlin/unit/generated/Example2c.kt",
                myThing,
                myNestedThing
            )
        }

        "Example 3 - type erasure".config(enabled = false) {
            generateAllTests(
                ThingWithListAndSet::class,
                "src/test/kotlin/unit/generated/Example3a.kt"
            )
            generateSampleInstances(
                "src/test/kotlin/unit/generated/Example3b.kt",
                MyThing::class,
                LocalDate::class
            )
        }

        "Example 4 - customizing sample values".config(enabled = false) {
            val customSerializer = ExactClassSampleValueVisitor(
                Byte::class,
                listOf(Byte::class.qualifiedName!!, java.time.LocalDate::class.qualifiedName!!)
            ) {
                LocalDate.now().dayOfMonth.toString()
            }
            val customFactory = SampleInstanceFactory(
                customVisitors =  listOf(customSerializer)
            )
            customFactory.generateAllKotests(
                "src/test/kotlin/unit/generated/Example4.kt",
                WithByte::class
            )
        }

        "Example 5 - customizing actual values".config(enabled = false) {
            val customLocalDateTimeSerializer = ExactClassSerializer(
                LocalDateTime::class,
                classesToImport = listOf(
                    LocalDateTime::class,
                    LocalDate::class,
                    LocalTime::class
                )
            ) { value: Any ->
                with(value as LocalDateTime) {
                    "LocalDateTime.of(\nLocalDate.of(${year}, ${monthValue}, ${dayOfMonth}),\nLocalTime.of(${hour}, ${minute}, ${second}, ${nano})\n)"
                }
            }
            val customizedFactory = ActualInstanceFactory(
                customSerializers = listOf(customLocalDateTimeSerializer)
            )
            customizedFactory.serializeToKotlin(
                "src/test/kotlin/unit/generated/Example5.kt",
                LocalDateTime.of(2021, 12, 28, 1, 2, 3, 4)
            )
        }

        "Example 6 - classes supported by default for sample values".config(enabled = false) {
            DefaultSampleValueVisitor().supportedClasses().forEach {
                println(it)
            }
        }

        "Example 7 - classes supported by default for actual values".config(enabled = false) {
            DefaultClassesSerializerFactory.supportedClasses
                .map { it.qualifiedName!! }
                .sorted()
                .forEach {
                    println(it)
                }
        }

        "Example 8 - serialize instance to assertions".config(enabled = false) {
            val myThing = MyThing(
                name = "Sample String",
                weight = BigDecimal("123")
            )
            val myList = listOf(
                MyThingWithPrivateWeight(
                    name = "Another String",
                    weight = BigDecimal("321")
                )
            )
            serializeToAssertions(
                "src/test/kotlin/unit/generated/Assertons0.kt",
                myThing,
                myList,
                SampleData.nestedThing()
            )
        }

        "Example 9 - serialize instance to mocks".config(enabled = false) {
            val myThing = MyThing(
                name = "Sample String",
                weight = BigDecimal("123")
            )
            val myList = listOf(
                MyThingWithPrivateWeight(
                    name = "Another String",
                    weight = BigDecimal("321")
                )
            )
            serializeToMocks(
                "src/test/kotlin/unit/generated/Mocks0.kt",
                myThing,
                myList,
                SampleData.nestedThing()
            )
            serializeToParameterizedMocks(
                "src/test/kotlin/unit/generated/ParameterizedMocks.kt",
                myThing,
                myList,
                SampleData.nestedThing()
            )
        }

        "Example 10 - serialize instance with fields that are collections".config(enabled = false) {
            val instance = SampleCollections(
                name = "Example",
                myList = listOf("Amber", LocalTime.of(12, 34, 56)),
                mySet = setOf(BigDecimal.ONE, LocalDate.of(2022, 1, 11)),
                myMap = mapOf(
                    "Ten" to BigDecimal.TEN,
                    2 to "twice"
                )
            )
            serializeToKotlin(
                "src/test/kotlin/unit/generated/CollectionsToCode.kt",
                instance
            )
            serializeToAssertions(
                "src/test/kotlin/unit/generated/CollectionsToAssertions.kt",
                instance
            )
            serializeToMocks(
                "src/test/kotlin/unit/generated/CollectionsToMock.kt",
                instance
            )
        }

        "Example 11 parameterized tests".config(enabled = false) {
            val testCases: List<List<Any?>> = testCases()
            generateParamsKotest(
                "src/test/kotlin/unit/generated/GeneratedParamsTestHappyPath.kt",
                instanceToTest = MyCalendar(setOf()),
                callableToTest = MyCalendar::advance,
                params = testCases
            )
        }
    }

    fun testCases(): List<List<Any?>> {
        val startDays = (7..13).asSequence()
        val rows = startDays.map {
            val date = LocalDate.of(2022, 2, it)
            listOf(listOf(date, 1), listOf(date, 2))
        }.flatten().toList()
        return rows
    }

    enum class FruitSize { SMALL, BIG }

    enum class FruitKind { APPLE, ORANGE }

    data class MyFruit(val size: FruitSize, val kind: FruitKind)
}