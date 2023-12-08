package io.kotest.generation.actual

import io.kotest.generation.MyThing
import io.kotest.generation.MyThingWithNoPublicConstructor
import io.kotest.generation.MyThingWithPrivatPrimaryConstructor
import io.kotest.generation.ServiceNotADataClass
import io.kotest.generation.common.CodeSnippet
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class PrimaryConstructorFieldsVisitorKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest
    private val actualInstanceFactory = ActualInstanceFactory()
    private val buffer = CodeSnippet()

    private val sut = PrimaryConstructorFieldsVisitor(actualInstanceFactory)

    init {
        "serialize instance of data class" {
            val thing = MyThing("apple", BigDecimal.ONE)
            buffer.addText("val thing = ")
            val handled = sut.serialize(thing, buffer, true)
            assertSoftly {
                handled shouldBe true
                buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf(
                    "io.kotest.generation.MyThing",
                    "kotlin.String",
                    "java.math.BigDecimal"
                )
                buffer.sourceCode() shouldBe listOf(
                    "val thing = MyThing(",
                    "name = \"\"\"apple\"\"\",",
                    "weight = BigDecimal(\"1\")",
                    ")"
                )
            }
        }

        "serialize instance of non-data class with primary constructor" {
            val thing = ServiceNotADataClass("big")
            buffer.addText("val thing = ")
            val handled = sut.serialize(thing, buffer, true)
            assertSoftly {
                handled shouldBe true
                buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf(
                    "io.kotest.generation.ServiceNotADataClass",
                    "kotlin.String"
                )
                buffer.sourceCode() shouldBe listOf(
                    "val thing = ServiceNotADataClass(",
                    "size = \"\"\"big\"\"\"",
                    ")"
                )
            }
        }

        "do not handle if no public constructor" {
            val thing = MyThingWithNoPublicConstructor.of(BigDecimal.ONE, "banana")
            val handled = sut.serialize(thing, buffer, true)
            assertSoftly {
                handled shouldBe false
                buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf()
                buffer.sourceCode() shouldBe listOf()
            }
        }

        "handle if primary constructor is private but there is a public one" {
            val thing = MyThingWithPrivatPrimaryConstructor(BigDecimal.ONE, "banana")
            val handled = sut.serialize(thing, buffer, true)
            assertSoftly {
                handled shouldBe true
                buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf(
                    "io.kotest.generation.MyThingWithPrivatPrimaryConstructor",
                    "kotlin.String",
                    "java.math.BigDecimal"
                )
                buffer.sourceCode() shouldBe listOf(
                    "MyThingWithPrivatPrimaryConstructor(",
                    "weight = BigDecimal(\"1\"),",
                    "name = \"\"\"banana\"\"\"",
                    ")"
                )
            }
        }
    }
}