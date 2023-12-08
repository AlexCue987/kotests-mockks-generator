package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.mockk.*

class CustomizedSampleValueVisitorKotest: StringSpec() {
    private val visitor1 = mockk<SampleSerializerVisitor>()
    private val visitor2 = mockk<SampleSerializerVisitor>()
    private val buffer = mockk<CodeSnippet>(relaxed = true)

    private val sut = CustomizedSampleValueVisitor(
        listOf(visitor1, visitor2)
    )

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        clearMocks(visitor1, visitor2)
        listOf(visitor1, visitor2).forEach {
            every { it.canHandle(any()) } returns false
            every { it.serialize(any(), any(), any()) } returns false
            justRun { it.handle(any(), any(), any()) }
        }
    }

    init {
        "canHandle works for empty list of visitors" {
            val noVisitors = CustomizedSampleValueVisitor(
                listOf()
            )
            sut.canHandle(Int::class) shouldBe false
            sut.serialize(Int::class, buffer, false) shouldBe false
        }

        "canHandle false" {
            sut.canHandle(Int::class) shouldBe false
        }

        "canHandle true" {
            every { visitor2.canHandle(any()) } returns true
            sut.canHandle(Int::class) shouldBe true
        }

        "if handled by first, second visitor not invoked" {
            every { visitor1.canHandle(any()) } returns true
            every { visitor1.serialize(any(), any(), any()) } returns true
            sut.serialize(Int::class, buffer, false) shouldBe true
            verify(exactly = 1) {
                visitor1.serialize(any(), any(), any())
            }
            verify(exactly = 0) {
                visitor2.serialize(any(), any(), any())
            }
        }

        "handled by second, first visitor invoked" {
            every { visitor2.canHandle(any()) } returns true
            every { visitor2.serialize(any(), any(), any()) } returns true
            sut.serialize(Int::class, buffer, false) shouldBe true
            verify(exactly = 1) {
                visitor1.serialize(any(), any(), any())
            }
            verify(exactly = 1) {
                visitor2.serialize(any(), any(), any())
            }
        }
    }
}