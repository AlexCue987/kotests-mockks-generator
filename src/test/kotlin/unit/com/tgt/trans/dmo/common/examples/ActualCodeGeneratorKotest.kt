package com.tgt.trans.dmo.common.examples

import com.tgt.trans.dmo.common.generation.ComplexThing
import com.tgt.trans.dmo.common.generation.MyThing
import com.tgt.trans.dmo.common.generation.MyThingWithPrivateWeight
import com.tgt.trans.dmo.common.generation.actual.ActualInstanceFactory
import com.tgt.trans.dmo.common.generation.actual.ExactClassSerializer
import com.tgt.trans.dmo.common.generation.actual.SampleData
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToAssertions
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToKotlin
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToMocks
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToParameterizedMocks
import com.tgt.trans.dmo.common.generation.common.createFolder
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ActualCodeGeneratorKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        createFolder("src/test/kotlin/generated")
    }

    init {
        "basic example".config(enabled = false) {
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
            serializeToKotlin(
                "src/test/kotlin/generated/DiffInOuterField.kt",
                myThing,
                myList,
                SampleData.nestedThing()
            )
            val myThingWithPrivateWeight = MyThingWithPrivateWeight("apple", BigDecimal.TEN)
            serializeToAssertions(
                "src/test/kotlin/generated/Assertons0.kt",
                myThingWithPrivateWeight,
                myThing,
                myList,
                SampleData.nestedThing(),
                LocalTime.of(12, 45)
            )
            serializeToMocks(
                "src/test/kotlin/generated/Mockks0.kt",
                myThingWithPrivateWeight,
                myThing,
                myList,
                SampleData.nestedThing(),
                LocalTime.of(12, 45)
            )
            serializeToParameterizedMocks(
                "src/test/kotlin/generated/Mockks0.kt",
                myThingWithPrivateWeight,
                myThing,
                SampleData.nestedThing()
            )
        }

        "serializeToKotlin - more complex cases".config(enabled = false) {
            val complexThing = ComplexThing(
                name = "Complex name",
                orderedItems = listOf(
                    SampleData.myThing().copy(name = "ordered 1"),
                    SampleData.myThing().copy(weight = BigDecimal.ONE)
                ),
                distinctItems = setOf(
                    SampleData.myThingWithPrivateWeight().copy(name = "with private 1"),
                    SampleData.myThingWithPrivateWeight().copy(name = "New Name")
                ),
                mappedThings = mapOf(
                    SampleData.fruit() to SampleData.nestedThing(),
                    SampleData.fruit().copy(name = "Banana") to
                            SampleData.nestedThing().copy(name = "Changed Name"),
                )
            )
            val someList = listOf(
                SampleData.myThing().copy(name = "list 1"),
                SampleData.myThing().copy(name = "list 2", weight = BigDecimal.ONE)
            )
            val someMap = mapOf(
                SampleData.fruit().copy(name = "lemon") to SampleData.nestedThing().copy(name = "van"),
                SampleData.fruit().copy(name = "pear") to
                        SampleData.nestedThing().copy(name = "horse"),
            )
            serializeToKotlin(
                "src/test/kotlin/generated/Serialized2.kt",
                complexThing,
                someList,
                LocalTime.of(12, 45),
                someMap
            )
        }

        "non-customized serializing LocalDateTime".config(enabled = false) {
            serializeToKotlin(
                "src/test/kotlin/generated/LocalDateTime_Default.kt",
                LocalDateTime.of(2021, 12, 28, 1,2, 3, 4)
            )
        }

        "serializing LocalDateTime rounded to minute".config(enabled = false) {
            val roundDownLocalDateTimeSerializer = ExactClassSerializer(
                LocalDateTime::class
            ) { value: Any ->
                with(value as LocalDateTime) {
                    "LocalDateTime.of(${year}, ${monthValue}, ${dayOfMonth}, ${hour}, ${minute}, 0)"
                }
            }
            val customizedFactory = ActualInstanceFactory(
                customSerializers = listOf(roundDownLocalDateTimeSerializer)
            )
            customizedFactory.serializeToKotlin(
                "src/test/kotlin/generated/LocalDateTime_Rounded.kt",
                LocalDateTime.of(2021, 12, 28, 1,2, 3, 4)
            )
        }

        "customized serializing LocalDateTime".config(enabled = false) {
            val customLocalDateTimeSerializer = ExactClassSerializer(
                LocalDateTime::class,
                classesToImport = listOf(LocalDateTime::class, LocalDate::class, LocalTime::class)
            ) { value: Any ->
                with(value as LocalDateTime) {
                    "LocalDateTime.of(\nLocalDate.of(${year}, ${monthValue}, ${dayOfMonth}),\nLocalTime.of(${hour}, ${minute}, ${second}, ${nano})\n)"
                }
            }
            val customizedFactory = ActualInstanceFactory(
                customSerializers = listOf(customLocalDateTimeSerializer)
            )
            customizedFactory.serializeToKotlin(
                "src/test/kotlin/generated/LocalDateTime_Customized.kt",
                LocalDateTime.of(2021, 12, 28, 1, 2, 3)
            )
        }
    }
}