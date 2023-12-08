package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SerializersListKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest
    private val buffer = CodeSnippet()
    private val serializer1 = mockk<SerializerVisitor>()
    private val serializer2 = mockk<SerializerVisitor>()
    private val sut = SerializersList(listOf(serializer1, serializer2))


    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

    }

    init {
        "invoke only first serializer" {
            every { serializer1.serialize(any(), any(), any()) } answers {
                buffer.addClassName("class")
                buffer.addLine("line")
                true
            }
            val actual = sut.serialize("Instance", buffer, false)
            assertSoftly {
                actual shouldBe true
                buffer.qualifiedNames() shouldBe listOf("class")
                buffer.sourceCode() shouldBe listOf("line")
                verify(exactly = 0) {
                    serializer2.serialize(any(), any(), any())
                }
            }
        }

        "invoke both serializers, second should handle" {
            every { serializer1.serialize(any(), any(), any()) } returns false
            every { serializer2.serialize(any(), any(), any()) } answers {
                buffer.addClassName("class")
                buffer.addLine("line")
                true
            }
            val actual = sut.serialize("Instance", buffer, false)
            assertSoftly {
                actual shouldBe true
                buffer.qualifiedNames() shouldBe listOf("class")
                buffer.sourceCode() shouldBe listOf("line")
                verify(exactly = 1) {
                    serializer1.serialize(any(), any(), any())
                }
                verify(exactly = 1) {
                    serializer2.serialize(any(), any(), any())
                }
             }
        }

        "invoke both serializers, none should handle" {
            every { serializer1.serialize(any(), any(), any()) } returns false
            every { serializer2.serialize(any(), any(), any()) } returns false
            val actual = sut.serialize("Instance", buffer, false)
            assertSoftly {
                actual shouldBe false
                buffer.qualifiedNames() shouldBe listOf()
                buffer.sourceCode() shouldBe listOf()
                verify(exactly = 1) {
                    serializer1.serialize(any(), any(), any())
                }
                verify(exactly = 1) {
                    serializer2.serialize(any(), any(), any())
                }
            }
        }
    }
}