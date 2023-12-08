package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ExactClassSerializerKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest
    private val buffer = CodeSnippet()

    private val sut = ExactClassSerializer(LocalDate::class) { value: Any ->
        with(value as LocalDate) {
            "LocalDate.of(${year}, ${monthValue}, ${dayOfMonth})"
        }
    }

    init {
        "handle exact type" {
            val actual = sut.serialize(LocalDate.of(2021, 12, 27), buffer, true)
            assertSoftly {
                actual shouldBe true
                buffer.qualifiedNames() shouldBe listOf("java.time.LocalDate")
                buffer.sourceCode() shouldBe listOf("LocalDate.of(2021, 12, 27)")
            }
        }

        "do not handle another type" {
            val actual = sut.serialize("Not a LocalDate", buffer, true)
            assertSoftly {
                actual shouldBe false
                buffer.qualifiedNames() shouldBe listOf()
                buffer.sourceCode() shouldBe listOf()
            }
        }

        "do not handle null" {
            val actual = sut.serialize(null, buffer, true)
            assertSoftly {
                actual shouldBe false
                buffer.qualifiedNames() shouldBe listOf()
                buffer.sourceCode() shouldBe listOf()
            }
        }

        "can provide mode than one class to import" {
            val serializerProvidingThreeClasses = ExactClassSerializer(
                LocalDateTime::class,
                listOf(LocalDateTime::class, LocalDate::class, LocalTime::class)
            ) { value: Any ->
                with(value as LocalDateTime) {
                    "LocalDateTime.of(LocalDate.of(${year}, ${monthValue}, ${dayOfMonth}), LocalTime.of(${hour}, ${minute}, ${second}, ${nano}))"
                }
            }
            val actual = serializerProvidingThreeClasses.serialize(LocalDateTime.of(2021, 12, 27, 1, 2, 3), buffer, true)
            assertSoftly {
                actual shouldBe true
                buffer.qualifiedNames() shouldContainExactlyInAnyOrder listOf(
                    "java.time.LocalDateTime", "java.time.LocalDate", "java.time.LocalTime"
                )
                buffer.sourceCode() shouldBe listOf("LocalDateTime.of(LocalDate.of(2021, 12, 27), LocalTime.of(1, 2, 3, 0))")
            }
        }
    }
}