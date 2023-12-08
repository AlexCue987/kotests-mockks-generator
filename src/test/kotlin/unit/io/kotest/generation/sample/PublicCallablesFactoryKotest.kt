package io.kotest.generation.sample

import io.kotest.generation.Box
import io.kotest.generation.MyThingWithPrivateWeight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class PublicCallablesFactoryKotest: StringSpec() {
    private val sut = PublicCallablesFactory()
    init {
        "callablesToMock" {
            sut.callablesToMock(Box::class) shouldContainExactlyInAnyOrder listOf(
                Box::length,
                Box::width,
                Box::height,
                Box::volume,
                Box::flipOverLength
            )
        }

        "callablesToTest" {
            sut.callablesToTest(Box::class) shouldContainExactlyInAnyOrder listOf(
                Box::volume,
                Box::flipOverLength
            )
        }

        "functionsNotToTest" {
            sut.functionsNotToTest() shouldContainExactlyInAnyOrder listOf(
                "copy", "toString", "equals", "hashCode"
            )
        }

        "primaryConstructorFields" {
            sut.primaryConstructorFields(MyThingWithPrivateWeight::class) shouldContainExactlyInAnyOrder
                    listOf("name", "weight")
        }
    }
}