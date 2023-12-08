package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SampleCollectionVisitorKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    private val sut = SampleCollectionVisitor()
    private val buffer = CodeSnippet()

    init {
        "canHandle true" {
            assertSoftly {
                sut.canHandle(listOf(1)::class) shouldBe true
                sut.canHandle(setOf(1)::class) shouldBe true
                sut.canHandle(mapOf("one" to 1)::class) shouldBe true
            }
        }

        "canHandle false for other class" {
            sut.canHandle(Byte::class) shouldBe false
        }

        "serialize list" {
            sut.serialize(listOf(1)::class, buffer, true)
            buffer.qualifiedNames() shouldBe listOf("kotlin.collections.List")
            buffer.sourceCode() shouldBe listOf("listOf()")
        }

        "serialize set" {
            sut.serialize(setOf(1)::class, buffer, true)
            buffer.qualifiedNames() shouldBe listOf("kotlin.collections.Set")
            buffer.sourceCode() shouldBe listOf("setOf()")
        }

        "serialize map" {
            sut.serialize(mapOf(1 to 2)::class, buffer, true)
            buffer.qualifiedNames() shouldBe listOf("kotlin.collections.Map")
            buffer.sourceCode() shouldBe listOf("mapOf()")
        }

        "isList" {
            assertSoftly {
                SampleCollectionVisitor.isList(listOf(1)::class) shouldBe true
                SampleCollectionVisitor.isList(setOf(1)::class) shouldBe false
            }
        }

        "isSet" {
            assertSoftly {
                SampleCollectionVisitor.isSet(setOf(1)::class) shouldBe true
                SampleCollectionVisitor.isSet(listOf(1)::class) shouldBe false
            }
        }

        "isMap" {
            assertSoftly {
                SampleCollectionVisitor.isMap(mapOf(1 to 2)::class) shouldBe true
                SampleCollectionVisitor.isMap(setOf(1)::class) shouldBe false
            }
        }
    }
}