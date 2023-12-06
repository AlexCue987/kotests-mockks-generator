package com.tgt.trans.dmo.common.generation.common

import com.tgt.trans.common.testhelpers.collections.matchLists
import com.tgt.trans.dmo.common.generation.*
import com.tgt.trans.dmo.common.generation.generators.actual.serializeToKotlin
import com.tgt.trans.dmo.common.generation.kotest.parameterToValueAssignment
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.collections.Set

class CodeSnippetFactoryKotest: StringSpec() {
    val sut = CodeSnippetFactory()
    init {
        "handle data class without collections or nested classes" {
            val actual = sampleInstance(MyThing::class, sut)
            assertSoftly {
                actual.qualifiedNames().toSet() shouldBe setOf(
                    "java.math.BigDecimal",
                    "com.tgt.trans.dmo.common.generation.MyThing",
                    "kotlin.String"
                )
                actual.sourceCode() shouldBe listOf(
                    "MyThing(",
                    "name = \"Whatever\",",
                    "weight = BigDecimal(\"42\")",
                ")"
                )
            }
        }

        "handle data class with collections" {
            val actual = sampleInstance(ThingWithCollections::class, sut)
            assertSoftly {
                actual.qualifiedNames().toSet() shouldBe setOf(
                    "com.tgt.trans.dmo.common.generation.ThingWithCollections",
                    "kotlin.String",
                    "java.time.LocalDateTime",
                    "kotlin.collections.Set",
                    "kotlin.collections.List",
                    "kotlin.collections.Map"
                )
                actual.sourceCode() shouldBe listOf(
                    "ThingWithCollections(",
                    "name = \"Whatever\",",
                    "createdAt = LocalDateTime.of(2021, 10, 25, 12, 34, 56),",
                    "attributes = setOf(),",
                    "importantDates = listOf(),",
                    "measurements = mapOf()",
                    ")"
                )
            }
        }

        "handle nested data class" {
            val actual = sampleInstance(NestedThing::class, sut)
            assertSoftly {
                actual.qualifiedNames().toSet() shouldBe setOf(
                    "com.tgt.trans.dmo.common.generation.NestedThing",
                    "com.tgt.trans.dmo.common.generation.MyThing",
                    "java.math.BigDecimal",
                    "java.math.BigInteger",
                    "kotlin.String",
                    "java.time.LocalDateTime",
                )
                actual.sourceCode() shouldBe listOf(
                    "NestedThing(",
                    "name = \"Whatever\",",
                    "weight = BigDecimal(\"42\"),",
                    "counter = BigInteger.valueOf(42L),",
                    "createdAt = LocalDateTime.of(2021, 10, 25, 12, 34, 56),",
                    """
myThing = MyThing(
name = "Whatever",
weight = BigDecimal("42")
)
                    """.trimIndent(),
                    ")"
                )
            }

        }

        "sampleValue" {
            sut.sampleValueForSupportedClass(Int::class) shouldBe "42"
        }

        "withAdditionalSampleValues adds new class" {
            val sampleThing = "MyThing.valueOf(\"banana\")"
            val systemToTest = CodeSnippetFactory
                .withAdditionalSampleValues(MyThing::class to { sampleThing })
            systemToTest.sampleValueForSupportedClass(MyThing::class) shouldBe sampleThing
        }

        "withAdditionalSampleValues replaces existing class" {
            val sampleInt = "41"
            val systemToTest = CodeSnippetFactory
                .withAdditionalSampleValues(Int::class to { sampleInt })
            systemToTest.sampleValueForSupportedClass(Int::class) shouldBe sampleInt
        }

        "avoid stackoverflow on nested class" {
            val buffer = CodeSnippet()
            CodeSnippetFactory().addValue(
                Name::class, buffer,
                true,
                ::parameterToValueAssignment,
                parameterName = "name")
//            serializeToKotlin("aaa.kt", buffer.sourceCode())
            matchLists(
                listOf(
                    """name = run {
val ret = mockk<Name>(relaxed = true)

ret
}"""
                ),
                buffer.sourceCode()
            )
        }
    }

    class Name(
        val name: String
    ) {
        var same: Name = this
    }
}