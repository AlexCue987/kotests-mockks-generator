package io.kotest.generation.sample

import com.tgt.trans.common.testhelpers.collections.matchLists
import com.tgt.trans.common.testhelpers.collections.matchSets
import io.kotest.generation.common.CodeSnippet
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.ZoneId

class LastResortSimpleMockVisitorKotest: StringSpec() {
    private val systemToTest = LastResortSimpleMockVisitor()

    init {
        "mockks a class" {
            val buffer = CodeSnippet()
            systemToTest.handle(MyClass::class, buffer, false)
            matchLists(
                listOf(
                    "run {",
                    "val ret = mockk<MyClass>(relaxed = true)",
                    "",
                    "ret",
                    "},"
                ),
                buffer.sourceCode()
            )
        }

        "mockks an interface" {
            val buffer = CodeSnippet()
            systemToTest.handle(MyInterface::class, buffer, false)
//            serializeToKotlin("aaa.txt", buffer.sourceCode())
            matchLists(
                listOf(
                    """run {""",
                    """val ret = mockk<MyInterface>(relaxed = true)""",
                    """""",
                    """ret""",
                    """},"""
            ),
                buffer.sourceCode()
            )
        }

        "computes classes to import" {
            val buffer = CodeSnippet()
            systemToTest.handle(Location::class, buffer, false)
//            serializeToKotlin("aaa.txt", buffer.qualifiedNames())
            matchSets(
                setOf(
                    """io.mockk.every""",
                    """io.mockk.mockk""",
                    """io.mockk.justRun""",
                    """io.kotest.generation.sample.LastResortSimpleMockVisitorKotest.Location""",
                ),
                buffer.qualifiedNames()
            )
        }
    }

    class MyClass(val name: String)

    interface MyInterface {
        val name: String
        val quantity: Int
    }

    interface Location {
        fun timeZone(): ZoneId
        val latitude: BigDecimal
    }
}