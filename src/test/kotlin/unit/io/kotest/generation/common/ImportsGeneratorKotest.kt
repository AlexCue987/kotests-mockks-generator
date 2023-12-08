package io.kotest.generation.common

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ImportsGeneratorKotest: StringSpec() {
    private val generatorToTest = ImportsGenerator()

    init {
        "sorts distinct imports" {
            val systemToTest = mockk<HasImports>()
            every { systemToTest.qualifiedNames() } returns listOf("shapes.Square", "shapes.Circle", "angles.Right")
            val unitTest = mockk<HasImports>()
            every { unitTest.qualifiedNames() } returns listOf("shapes.Square", "angles.Acute", "angles.Right")

            val actual = generatorToTest.generate(systemToTest, unitTest)

            actual shouldBe listOf("angles.Acute", "angles.Right", "shapes.Circle", "shapes.Square")
        }
    }
}