package io.kotest.generation.actual

import io.kotest.generation.ComplexThing
import io.kotest.generation.MyThing
import io.kotest.generation.MyThingWithNullableFields
import io.kotest.generation.ThingWithListAndSet
import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.*

class ActualInstanceFactoryAddValueKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    private val customSerializer = ExactClassSerializer(String::class) {
        "\"My Custom String\""
    }

    private val sut = ActualInstanceFactory(
        customSerializers = listOf(customSerializer)
    )

    init {
        "addValue handles null" {
            val buffer = CodeSnippet()
            sut.addValue(null, buffer, false, "fieldName = ")
            buffer.sourceCode() shouldBe listOf("fieldName = null,")
            buffer.qualifiedNames().isEmpty() shouldBe true
        }

        "addValue handles customized serializer" {
            val buffer = CodeSnippet()
            sut.addValue("Some String", buffer, false, "fieldName = ")
            buffer.sourceCode() shouldBe listOf("fieldName = \"My Custom String\",")
            buffer.qualifiedNames() shouldBe listOf("kotlin.String")
        }

        "addValue handles instance with default serializer" {
            val buffer = CodeSnippet()
            sut.addValue(BigDecimal.ONE, buffer, false, "fieldName = ")
            buffer.sourceCode() shouldBe listOf("fieldName = BigDecimal(\"1\"),")
            buffer.qualifiedNames() shouldBe listOf("java.math.BigDecimal")
        }

        "addValue handles enum" {
            val buffer = CodeSnippet()
            sut.addValue(DayOfWeek.SUNDAY, buffer, false, "fieldName = ")
            buffer.sourceCode() shouldBe listOf("fieldName = DayOfWeek.SUNDAY,")
            buffer.qualifiedNames() shouldBe listOf("java.time.DayOfWeek")
        }

        "addValue handles data class" {
            val buffer = CodeSnippet()
            sut.addValue(MyThing("pear", BigDecimal("2")), buffer, false, "fieldName = ")
            buffer.sourceCode() shouldBe listOf(
                "fieldName = MyThing(",
                "name = \"My Custom String\",",
                "weight = BigDecimal(\"2\")",
                "),"
            )
            buffer.qualifiedNames() shouldContainExactlyInAnyOrder  listOf(
                "io.kotest.generation.MyThing",
                "kotlin.String",
                "java.math.BigDecimal"
            )
        }

        "handle instance with null fields" {
            val sut = ActualInstanceFactory(
                customSerializers = listOf()
            )
            val buffer = CodeSnippet()
            val value = MyThingWithNullableFields(name = "apple", weight = null, bestBefore = null)
            sut.addValue(value, buffer, false, "fieldName = ")
            buffer.sourceCode() shouldBe listOf(
                "fieldName = MyThingWithNullableFields(",
                "name = \"\"\"apple\"\"\",",
                "weight = null,",
                "bestBefore = null",
                "),"
            )
            buffer.qualifiedNames() shouldContainExactlyInAnyOrder  listOf(
                "io.kotest.generation.MyThingWithNullableFields",
                "kotlin.String"
            )
        }

        "handle collections as fields" {
            val thing = ThingWithListAndSet(
                name = "Sample String",
                createdAt = LocalDateTime.of(2021, 12, 28, 11, 12),
                attributes = setOf(
                    SampleData.myThing(),
                    SampleData.myThing().copy(weight = BigDecimal.ONE)
                ),
                importantDates = listOf(
                    LocalDate.of(2022, 2, 3),
                    LocalDate.of(2022, 3, 4),
                )
            )
            val buffer = CodeSnippet()
            sut.addValue(thing, buffer, true)
//            buffer.sourceCode().forEach {
//                println("\"\"\"$it\"\"\",")
//            }
//            buffer.qualifiedNames().forEach {
//                println("\"$it\",")
//            }
            buffer.sourceCode() shouldBe listOf(
                """ThingWithListAndSet(""",
                """name = "My Custom String",""",
                """createdAt = LocalDateTime.of(2021, 12, 28, 11, 12, 0, 0),""",
                """attributes = setOf(""",
                """MyThing(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("41")""",
                """),""",
                """MyThing(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("1")""",
                """)""",
                """),""",
                """importantDates = listOf(""",
                """LocalDate.of(2022, 2, 3),""",
                """LocalDate.of(2022, 3, 4)""",
                """)""",
                """)""",
            )
            buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf(
                "kotlin.String",
                "java.time.LocalDateTime",
                "io.kotest.generation.MyThing",
                "java.math.BigDecimal",
                "java.time.LocalDate",
                "io.kotest.generation.ThingWithListAndSet",
            )
        }

        "handle data class with lists, sets, and maps as fields" {
            val thing = ComplexThing(
                name = "Sample String",
                orderedItems = listOf(
                    SampleData.myThing(),
                    SampleData.myThing().copy(weight = BigDecimal.ONE)
                ),
                distinctItems = setOf(
                    SampleData.myThingWithPrivateWeight(),
                    SampleData.myThingWithPrivateWeight().copy(name = "New Name")
                ),
                mappedThings = mapOf(
                    SampleData.fruit() to SampleData.nestedThing()
                )
            )
            val buffer = CodeSnippet()
            sut.addValue(thing, buffer, true)
//            buffer.sourceCode().forEach {
//                println("\"\"\"$it\"\"\",")
//            }
//            buffer.qualifiedNames().forEach {
//                println("\"$it\",")
//            }
            buffer.sourceCode() shouldBe listOf(
                """ComplexThing(""",
                """name = "My Custom String",""",
                """orderedItems = listOf(""",
                """MyThing(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("41")""",
                """),""",
                """MyThing(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("1")""",
                """)""",
                """),""",
                """distinctItems = setOf(""",
                """MyThingWithPrivateWeight(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("41")""",
                """),""",
                """MyThingWithPrivateWeight(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("41")""",
                """)""",
                """),""",
                """mappedThings = mapOf(""",
                """Fruit(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("41"),""",
                """bestBefore = LocalDateTime.of(2021, 10, 25, 12, 34, 56, 0),""",
                """type = FruitType.FRESH""",
                """)""",
                """to""",
                """NestedThing(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("41"),""",
                """counter = BigInteger.valueOf(42L),""",
                """createdAt = LocalDateTime.of(2021, 10, 25, 12, 34, 56, 0),""",
                """myThing = MyThing(""",
                """name = "My Custom String",""",
                """weight = BigDecimal("41")""",
                """)""",
                """)""",
                """)""",
                """)""",
            )
            buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf(
                "kotlin.String",
                "io.kotest.generation.MyThing",
                "java.math.BigDecimal",
                "io.kotest.generation.MyThingWithPrivateWeight",
                "io.kotest.generation.Fruit",
                "java.time.LocalDateTime",
                "io.kotest.generation.FruitType",
                "io.kotest.generation.NestedThing",
                "java.math.BigInteger",
                "io.kotest.generation.ComplexThing",
            )
        }
    }
}