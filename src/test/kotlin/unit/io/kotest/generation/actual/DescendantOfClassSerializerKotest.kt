package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZoneOffset

class DescendantOfClassSerializerKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest
    private val buffer = CodeSnippet()

    private val sut = DescendantOfClassSerializer(ZoneId::class) { value: Any ->
        with(value as ZoneId) {
            "ZoneId.of(\"$id\")"
        }
    }

    init {
        "handle descendant" {
            val actual = sut.serialize(ZoneOffset.of("-07:30"), buffer, true)
            assertSoftly {
                actual shouldBe true
                buffer.qualifiedNames() shouldBe listOf("java.time.ZoneId")
                buffer.sourceCode() shouldBe listOf("ZoneId.of(\"-07:30\")")
            }
        }

        "skip non descendants" {
            listOf(BigDecimal.TEN, null).forEach {
                val actual = sut.serialize(BigDecimal.TEN, buffer, true)
                assertSoftly {
                    actual shouldBe false
                    buffer.qualifiedNames() shouldBe listOf()
                    buffer.sourceCode() shouldBe listOf()
                }
            }
        }

        "use explicit qualified names" {
            val qualifiedNames = listOf("kotlin.String, kotlin.Any")
            val sutWithExplicitQualifiedNames = DescendantOfClassSerializer(
                ZoneId::class,
                qualifiedNames
            ) { value: Any ->
                with(value as ZoneId) {
                    "ZoneId.of(\"$id\")"
                }
            }
            val actual = sutWithExplicitQualifiedNames.serialize(ZoneOffset.of("-07:30"), buffer, true)
            assertSoftly {
                actual shouldBe true
                buffer.qualifiedNames() shouldBe qualifiedNames
                buffer.sourceCode() shouldBe listOf("ZoneId.of(\"-07:30\")")
            }
        }
    }
}