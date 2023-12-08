package io.kotest.generation.common

import io.kotest.generation.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.reflect.*
import kotlin.reflect.full.primaryConstructor

class UtilsKotest: StringSpec() {
    init {
        "packageName" {
            packageName(ThingFactory::class) shouldBe "io.kotest.generation"
        }

        "methodParameters does not include this, as it has no name" {
            val actual = methodParameters(AnotherService::doSomething)
            actual.map { it.name } shouldBe listOf("time", "date")
        }

        "methodParameters does not include varargs" {
            val actual = methodParameters(AnotherService::funWithVarargs)
            actual.map { it.name } shouldBe listOf("name")
        }

        "isVoid true" {
            isVoid(AnotherService::voidFun) shouldBe true
            isVoid(AnotherService::anotherVoidFun) shouldBe true
        }

        "isVoid false" {
            isVoid(AnotherService::doSomething) shouldBe false
        }

        "variableNameOf" {
            variableNameOf(AnotherService::class) shouldBe "anotherService"
        }

        "isEnum true" {
            isEnum(FruitType::class) shouldBe true
        }

        "isEnum false" {
            isEnum(MyThing::class) shouldBe false
        }

        "isEnum handles interfaces" {
            isEnum(Set::class) shouldBe false
        }

        "firstEnumValue" {
            firstEnumValue(FruitType::class) shouldBe FruitType.FRESH.toString()
        }

        "isDescendantOf" {
            "Something".isDescendantOf(BigDecimal::class) shouldBe false
            BigDecimal.ONE.isDescendantOf(BigDecimal::class) shouldBe true
            listOf("A").isDescendantOf(List::class) shouldBe true
            listOf("A").isDescendantOf(Map::class) shouldBe false
        }

        "isList" {
            isList(listOf("a")) shouldBe true
            isList(listOf<String>()) shouldBe true
            isList(setOf("a")) shouldBe false
            isList(setOf<Int>()) shouldBe false
        }

        "isSet" {
            isSet(setOf("a")) shouldBe true
            isSet(setOf<String>()) shouldBe true
            isSet(listOf("a")) shouldBe false
            isSet(listOf<Int>()) shouldBe false
        }

        "isListOrSet" {
            isListOrSet(setOf("a")) shouldBe true
            isListOrSet(setOf<String>()) shouldBe true
            isListOrSet(listOf("a")) shouldBe true
            isListOrSet(listOf<Int>()) shouldBe true
            isListOrSet(mapOf(1 to "one")) shouldBe false
        }

        "isProperty" {
            isProperty(Box::volume) shouldBe true
            isProperty(Box::flipOverLength) shouldBe false
        }

        "isFunction" {
            isFunction(Box::volume) shouldBe false
            isFunction(Box::flipOverLength) shouldBe true
        }

        "all public callables" {
            val klass = Box::class
            val publicMethods: List<KCallable<*>> = klass.members
                .filter { it.visibility == KVisibility.PUBLIC &&
                it.name !in functionsNotToTest() &&
                        !it.name.startsWith("component")}
            publicMethods.forEach {
                println(it.name)
                val type = when{
                    it is KProperty -> "property"
                    it is KFunction -> "function"
                    else -> "unknown"
                }
                println(type)
                if(it.name.startsWith("component")){
                    println("component")
                }
            }
        }

    }
}

fun functionsNotToTest() = Any::class.members.map{ it.name }.toSet() + "copy"

fun primaryConstructorFields(klass: KClass<*>): List<String> =
    klass.primaryConstructor?.let { it.parameters.mapNotNull { it.name } } ?: listOf()
