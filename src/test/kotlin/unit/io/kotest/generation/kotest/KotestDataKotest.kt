package io.kotest.generation.kotest

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.mockk.every
import io.mockk.mockk

class KotestDataKotest: StringSpec() {
    init {
        "combines classes to import" {
            val method1 = mockk<MethodToTest>()
            every { method1.qualifiedNames() } returns listOf("fruit.Banana", "fruit.Apple")
            val method2 = mockk<MethodToTest>()
            every { method2.qualifiedNames() } returns listOf("fruit.Banana", "fruit.Orange")
            val systemToTest = mockk<SystemToTest>()
            every { systemToTest.qualifiedNames() } returns listOf("plant.Cactus")

            val actual = KotestData(systemToTest, listOf(method1, method2))

            actual.classesToImport shouldContainExactlyInAnyOrder listOf("fruit.Banana", "fruit.Apple", "fruit.Orange", "plant.Cactus")
        }
    }
}