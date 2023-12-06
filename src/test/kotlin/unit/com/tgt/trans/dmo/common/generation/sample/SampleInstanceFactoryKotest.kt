package com.tgt.trans.dmo.common.generation.sample

import com.tgt.trans.common.testhelpers.collections.matchElementsIgnoringOrder
import com.tgt.trans.dmo.common.generation.*
import com.tgt.trans.dmo.common.generation.actual.ExactClassSerializer
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class SampleInstanceFactoryKotest: StringSpec() {
    private val customSerializer = ExactClassSampleValueVisitor(Byte::class) {
        "1"
    }

    private val sut = SampleInstanceFactory(listOf(customSerializer))

    init {
        "custom serializer overrides default one" {
            val actual = sut.serializeSampleValue(Byte::class)
            assertSoftly {
                actual.qualifiedNames() shouldBe listOf("kotlin.Byte")
                actual.sourceCode() shouldBe listOf("1")
            }
        }

        "sample basic type" {
            val actual = sut.serializeSampleValue(BigDecimal::class)
            assertSoftly {
                actual.qualifiedNames() shouldBe listOf("java.math.BigDecimal")
                actual.sourceCode() shouldBe listOf("BigDecimal(\"42\")")
            }
        }

        "sample enum" {
            val actual = sut.serializeSampleValue(FruitType::class)
            assertSoftly {
                actual.qualifiedNames() shouldBe listOf("com.tgt.trans.dmo.common.generation.FruitType")
                actual.sourceCode() shouldBe listOf("FruitType.FRESH")
            }
        }

        "sample list" {
            val actual = sut.serializeSampleValue(List::class)
            assertSoftly {
                actual.qualifiedNames() shouldBe listOf("kotlin.collections.List")
                actual.sourceCode() shouldBe listOf("listOf()")
            }
        }

        "sample set" {
            val actual = sut.serializeSampleValue(Set::class)
            assertSoftly {
                actual.qualifiedNames() shouldBe listOf("kotlin.collections.Set")
                actual.sourceCode() shouldBe listOf("setOf()")
            }
        }

        "sample map" {
            val actual = sut.serializeSampleValue(Map::class)
            assertSoftly {
                actual.qualifiedNames() shouldBe listOf("kotlin.collections.Map")
                actual.sourceCode() shouldBe listOf("mapOf()")
            }
        }

        "sample data class" {
            val actual = sut.serializeSampleValue(MyThing::class)
            assertSoftly {
                actual.sourceCode() shouldBe listOf(
                    """MyThing(""",
                    """name = "Whatever",""",
                    """weight = BigDecimal("42")""",
                    """)""",
                )
                actual.qualifiedNames() shouldContainExactlyInAnyOrder  listOf(
                    "com.tgt.trans.dmo.common.generation.MyThing",
                    "kotlin.String",
                    "java.math.BigDecimal"
                )
            }
        }

        "sample nested data class" {
            val actual = sut.serializeSampleValue(NestedThing::class)
//            actual.sourceCode().forEach {
//                println("\"\"\"$it\"\"\",")
//            }
//            actual.qualifiedNames().forEach {
//                println("\"$it\",")
//            }
            assertSoftly {
                actual.sourceCode() shouldBe listOf(
                    """NestedThing(""",
                    """name = "Whatever",""",
                    """weight = BigDecimal("42"),""",
                    """counter = BigInteger.valueOf(42L),""",
                    """createdAt = LocalDateTime.of(2021, 10, 25, 12, 34, 56),""",
                    """myThing = MyThing(""",
                    """name = "Whatever",""",
                    """weight = BigDecimal("42")""",
                    """)""",
                    """)""",
                )
                actual.qualifiedNames() shouldContainExactlyInAnyOrder  listOf(
                    "kotlin.String",
                    "java.math.BigDecimal",
                    "java.math.BigInteger",
                    "java.time.LocalDateTime",
                    "com.tgt.trans.dmo.common.generation.MyThing",
                    "com.tgt.trans.dmo.common.generation.NestedThing",
                )
            }
        }

        "sample data class with collections as fields" {
            val actual = sut.serializeSampleValue(ComplexThing::class)
//            actual.sourceCode().forEach {
//                println("\"\"\"$it\"\"\",")
//            }
//            actual.qualifiedNames().forEach {
//                println("\"$it\",")
//            }
            assertSoftly {
                actual.sourceCode() shouldBe listOf(
                    """ComplexThing(""",
                    """name = "Whatever",""",
                    """orderedItems = listOf(),""",
                    """distinctItems = setOf(),""",
                    """mappedThings = mapOf()""",
                    """)""",
                )
                actual.qualifiedNames() shouldContainExactlyInAnyOrder  listOf(
                    "kotlin.String",
                    "kotlin.collections.List",
                    "kotlin.collections.Set",
                    "kotlin.collections.Map",
                    "com.tgt.trans.dmo.common.generation.ComplexThing",
                )
            }
        }

        "last resort - just mock it" {
            val actual = sut.serializeSampleValue(MyThingWithPrivatPrimaryConstructor::class)
            actual.sourceCode().forEach { println("\"\"\"$it\"\"\",") }
            assertSoftly {
                matchElementsIgnoringOrder(
                    listOf(
                        "io.mockk.every",
                        "io.mockk.mockk",
                        "io.mockk.justRun",
                        "com.tgt.trans.dmo.common.generation.MyThingWithPrivatPrimaryConstructor",
                    ),
                    actual.qualifiedNames()
                )
                actual.sourceCode() shouldBe listOf(
                    """run {""",
                    """val ret = mockk<MyThingWithPrivatPrimaryConstructor>(relaxed = true)""",
                    """""",
                    """ret""",
                    """}""",
                )
            }
        }
    }
}