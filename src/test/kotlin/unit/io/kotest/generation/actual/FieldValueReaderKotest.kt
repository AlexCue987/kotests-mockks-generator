package io.kotest.generation.actual

import io.kotest.generation.MyThing
import io.kotest.generation.MyThingWithPrivateWeight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.reflect.full.memberProperties

class FieldValueReaderKotest: StringSpec() {
    private val sut = FieldValueReader()

    private val weight = BigDecimal.ONE

    init {
        "fieldValue reads public field" {
            val name = "apple"
            val thing = MyThing(name, BigDecimal.ONE)
            val nameField = thing::class.memberProperties.find { it.name == "name" }
            sut.fieldValue(thing, nameField!!) shouldBe name
        }

        "fieldValue reads private field" {
            val thing = MyThingWithPrivateWeight("orange", weight)
            val nameField = thing::class.memberProperties.find { it.name == "weight" }
            sut.fieldValue(thing, nameField!!) shouldBe weight
        }

        "primaryConstructorParameters works" {
            val thing = MyThingWithPrivateWeight("orange", weight)
            val actual = sut.primaryConstructorParameters(thing)
            actual[0] shouldBe FieldValue("name", "orange", String::class, MyThingWithPrivateWeight::name)
            actual[1].name shouldBe "weight"
            actual[1].value shouldBe weight
            actual[1].klass shouldBe BigDecimal::class
            actual[1].field.name shouldBe "weight"
        }
    }
}