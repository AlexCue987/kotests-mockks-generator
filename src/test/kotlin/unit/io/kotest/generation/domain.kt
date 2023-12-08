package io.kotest.generation

import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*

data class Box(
    val length: BigDecimal,
    val width: BigDecimal,
    val height: BigDecimal
) {
    val volume: BigDecimal = length * width * height
    private val uuid: UUID = UUID.randomUUID()

    fun flipOverLength(): Box = throw NotImplementedError()
    private fun flipOverWidth(): Box = throw NotImplementedError()
}

interface HasName {
    val name: String
}

data class MyThing(
    override val name: String,
    val weight: BigDecimal
    ): HasName {
    companion object {
        fun valueOf(name: String) = MyThing(name, BigDecimal.ZERO)
    }
}

data class MyThingWithPrivateWeight(
    val name: String,
    private val weight: BigDecimal
)

data class MyThingWithNullableFields(
    val name: String,
    val weight: BigDecimal?,
    val bestBefore: LocalDate?
)

class MyThingWithPrivatPrimaryConstructor private constructor(
    val name: String,
    val weight: BigDecimal
) {
    constructor(weight: BigDecimal, name: String): this(name, weight)
}


class MyThingWithNoPublicConstructor private constructor(
    val name: String,
    val weight: BigDecimal
) {
    companion object {
        fun of(weight: BigDecimal, name: String) = MyThingWithNoPublicConstructor(name, weight)
    }
}

enum class FruitType { FRESH, FROZEN, CANNED, DRIED }

data class Fruit (
    val name: String,
    val weight: BigDecimal,
    val bestBefore: LocalDateTime,
    val type: FruitType
)

data class NestedThing(
    val name: String,
    val weight: BigDecimal,
    val counter: BigInteger,
    val createdAt: LocalDateTime,
    val myThing: MyThing
)

data class WithByte(val byte: Byte) {
    fun increment() = WithByte((byte + 1).toByte())
}

data class ComplexThing(
    val name: String,
    val orderedItems: List<MyThing>,
    val distinctItems: Set<MyThingWithPrivateWeight>,
    val mappedThings: Map<Fruit, NestedThing>
)

class ServiceNotADataClass(val size: String)

class AnotherService(
    val cacheTtl: Long,
    val effectiveDate: LocalDate,
    val resources: BigDecimal,
    val serviceNotADataClass: ServiceNotADataClass,
    val thing: MyThing
    ) {
    fun doSomething(time: LocalTime, date: LocalDate): NestedThing {
        throw NotImplementedError()
    }

    fun doSomethingElse(amount: Int, time: LocalTime): MyThing {
        throw NotImplementedError()
    }

    fun doOneMoreThing(thing: NestedThing, date: LocalDate): MyThing {
        throw NotImplementedError()
    }

    fun doSeveralThings(thing: NestedThing, date: LocalDate): List<MyThing> {
        throw NotImplementedError()
    }

    fun getDate(thing: NestedThing, date: LocalDate): LocalDate {
        throw NotImplementedError()
    }

    fun funWithVarargs(name: String, vararg otherNames: String): String {
        throw NotImplementedError()
    }

    fun voidFun(thing: NestedThing, date: LocalDate) {}

    fun anotherVoidFun(thing: NestedThing, date: LocalDate): Unit {}
}

class ServiceWithLotsOfParameters(val anotherService: AnotherService) {
    fun transformToInstance(input: ThingWithCollections): ThingWithCollections {
        throw NotImplementedError()
    }

    fun transformToBigDecimal(input: ThingWithCollections) = BigDecimal.TEN

    fun transformToInt(input: ThingWithCollections) = 42

    fun transformToLocalDate(input: ThingWithCollections) = LocalDate.of(2021, 11, 29)

    fun transformToList(input: ThingWithCollections): List<ThingWithCollections> {
        throw NotImplementedError()
    }
    fun transformToSet(input: ThingWithCollections): Set<ThingWithCollections> {
        throw NotImplementedError()
    }
    fun transformToMap(input: ThingWithCollections): Map<String, ThingWithCollections> {
        throw NotImplementedError()
    }
}

class ThingFactory(val quantity: Int, val name: String) {
    val myProperty: Int
        get() = throw NotImplementedError()

    fun apple(weight: BigDecimal): MyThing = MyThing("Apple", weight)

    fun orange(weight: Int): MyThing = MyThing("Orange", weight.toBigDecimal())
}

data class ThingWithListAndSet(
    val name: String,
    val createdAt: LocalDateTime,
    val attributes: Set<MyThing>,
    val importantDates: List<LocalDate>
) {
    fun mergeWith(other: ThingWithListAndSet): ThingWithListAndSet =
        throw NotImplementedError()
}


data class ThingWithCollections(
    val name: String,
    val createdAt: LocalDateTime,
    val attributes: Set<MyThing>,
    val importantDates: List<NestedThing>,
    val measurements: Map<LocalDateTime, BigDecimal>
)

data class SampleCollections(
    val name: String,
    val mySet: Set<Any>,
    val myList: List<Any>,
    val myMap: Map<Any, Any>
)

data class WithAllBasicTypes(
    val byte: Byte,
    val short: Short,
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val uByte: UByte,
    val uShort: UShort,
    val uInt: UInt,
    val uLong: ULong,
    val boolean: Boolean,
    val char: Char,
    val string: String
)

data class WithAllTemporaryTypes(
    val dayOfWeek: DayOfWeek,
    val instant: Instant,
    val localDate: LocalDate,
    val localTime: LocalTime,
    val localDateTime: LocalDateTime,
    val offsetDateTime: OffsetDateTime,
    val offsetTime: OffsetTime,
    val zoneId: ZoneId,
    val zoneOffset: ZoneOffset,
    val zonedDateTime: ZonedDateTime
)