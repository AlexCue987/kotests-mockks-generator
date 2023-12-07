# Generate tests and mocks, and serialize objects as Kotlin code

**Why:** if classes have many fields, developing tests and mocks is time-consuming. `kotests-mockks-generator` makes it faster.

**What:**
* for existing or new code, serialize actual objects into compilable Kotlin, in a format that is easy to use in tests
* for any class, generate compilable unit test skeletons off methods' signatures
* for any class, generate compilable mock statements off methods' signatures

### Plug it in

TBD

## Speed up creating data to use in tests 

Suppose we need to add a test for a function that returns a class with many fields, like this:
```kotlin
    fun doSomething(params: MyParams): SampleComplexClass {
        //(snip)
    }

    data class SampleComplexClass(
        val name: String,
        val box: Box,
        val orderedItems: List<Item>,
        val prioritizedItems: Map<Item, Int>
    )
```

**Note:** this class is intentionally kept small. In real life, all too often we need to deal with much larger ones.

Because this class has many fields, and some are nested, crafting test data by hand takes a lot of time.

### Serializing actual data

To speed up creating test data, we can serialize actual instances of `MyParams` and `SampleComplexClass` as follows:
```kotlin
val params = MyParams("some", "parameters")

val results = doSomething(params)
// serialize as constructor calls
serializeToKotlin(params, results)
// serialize as kotest assertions
serializeToAssertions(results)
// serialize as mocks
serializeToMocks(results)
```
The instances are serialized in three files: as constructor calls, as Kotest assertions, and as mocks.

### Serializing actual instances as constructor calls

When we invoke `serializeToKotlin` without explicitly specifying file name, it generates a file named `ActualMyParams.kt`. The output includes both `params` and `results` along with all needed imports, so that the output can compile:
```kotlin
import com.tgt.trans.dmo.common.examples.ReadmeExample0Test.SampleComplexClass
import java.math.BigDecimal
// all other needed imports

SampleOuterClass(
        name = """some name""",
    // serialized nested data class
        box = Box(
            length = BigDecimal("1"),
            width = BigDecimal("2"),
            height = BigDecimal("3")
        ),
    // serialized nested list
        orderedItems = listOf(
            Item(
                name = """apple""",
                quantity = 1
            )
        ),
    // serialized nested map
        prioritizedItems = mapOf(
            Item(
                name = """apple""",
                quantity = 1
            )
                    to
                    (snip)
```

In general, `serializeToKotlin` and all other methods in this library are not meant to run as part of a build.
We should invoke code generation manually, and then tweak and format and move generated code as needed.
The intent is to automate most of the work, not all of it. As such, there are edge cases that are not supported. They are documented later.

**Note:** we can explicitly specify file name:
```kotlin
val output = doSomething(input)
serializeToKotlin("ActualInstance.kt", input, output)
```


### Serializing actual instance as assertions

We can output this actual data directly as assertions, which can be easily added to a unit test:
```kotlin
serializeToAssertions("ActualInstanceAssertions.kt", actual)
```
We shall get this output. After formatting it looks as follows:
```kotlin
import java.math.BigDecimal
// all other needed imports

  assertSoftly {
     actual.name shouldBe """some name"""
     actual.box shouldBe Box(
         length = BigDecimal("1"),
         width = BigDecimal("2"),
         height = BigDecimal("3")
     )
     actual.orderedItems shouldBe listOf(
         Item(
             name = """apple""",
             quantity = 1
         )
     )
    (snip)
```

### Serializing actual instance as a mock

Also we can serialize actual data into a mock:
```kotlin
serializeToMocks(actual)
serializeToMocks("ActualInstanceMocks.kt", actual)
```
and the output is as follows:
```kotlin
import java.math.BigDecimal
// all other needed imports

    fun mock0(): SampleComplexClass {
        val ret = mockk<SampleComplexClass>()
        every { ret.name } returns """some name"""
        every { ret.box } returns Box(
            length = BigDecimal("1"),
            width = BigDecimal("2"),
            height = BigDecimal("3")
        )
    (snip)
        return ret
    }
```

**Note:** mocks are exposed as functions, so that every call produces a fresh copy, and multiple tests do not have to share a mutable mock - the mocks returned by the function are safe to use in multiple places.

### Generate sample data

Alternatively, we can generate sample data as follows:
```kotlin
// use default file name
generateSampleInstances(
    SampleComplexClass::class,
    Item::class
)
// or explicitly provide file name
generateSampleInstances(
    "SampleInstance.kt",
    SampleComplexClass::class,
    Item::class
)
```
and the output in file `SampleInstance.kt` looks as follows, after formatting:
```kotlin
object ActualInstance {
    fun sampleComplexClass() = SampleComplexClass(
        name = "Whatever",
        box = Box(
            length = BigDecimal("42"),
            width = BigDecimal("42"),
            height = BigDecimal("42")
        ),
        orderedItems = listOf(),
        prioritizedItems = mapOf()
    )

    fun item() = Item(
        name = "Whatever",
        quantity = 42
    )
}
```
We can cut and paste this output into our unit tests - it is much faster than typing all that manually.

[Complete example in ReadmeExample0Kotest](src/test/kotlin/unit/com/tgt/trans/dmo/common/examples/ReadmeExample0Kotest.kt)

**Note:** `kotest-generator` knows that `orderedItems` field is a `List`, but it does not know the type of its items.
This is why it generates `orderedItems = listOf()`. We need to generate a sample instance of `Item` ourselves, and paste it manually.

**Note:** by default, `kotest-generator` provides one and the same value for every field of the same type.
For instance, all `BigDecimal` fields get `BigDecimal("42")`. This behaviour can be customized, which is described later.

## Speed up greenfield development

Suppose we are creating a new class, and we have agreed on the contract: methods' and properties' names, parameters, and return types, as follows:

```kotlin
class ThingFactory(val quantity: Int, val name: String) {
    val myProperty: Int
        get() = throw NotImplementedError()

    fun apple(weight: BigDecimal): MyThing = throw NotImplementedError()

    fun orange(weight: Int): MyThing = MyThing("Orange", weight.toBigDecimal())
}
```

At this point we can start working in parallel: one engineer can start implementing `ThingFactory`, while someone else can start plugging it in.

To speed up development, let us generate some code:
```kotlin
// generate to default folder
generateAllTestsAndMockks(
    ThingFactory::class
)
// explicitly provide folder
generateAllTestsAndMockks(
    ThingFactory::class,
    folder = "src/test/kotlin/generated",
)
```
We have just generated:
* skeleton unit tests, in a file named `ThingFactoryTest.kt`.
* skeleton mocks, in a file named `MockkThingFactory.kt`.

Generated test suite has a test for every public method or property:
```kotlin
// all necessary imports

class ThingFactoryKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    private val systemToTest = ThingFactory(
        quantity = 42,
        name = "Whatever"
    )

    init {
        "myProperty works".config(enabled = false) {
            val actual = systemToTest.myProperty
            actual shouldBe 42
        }
        (snip)
```

In this simple example `systemToTest` has really simple parameters which are easy to serialize. 
In a more complex case, when we are generating tests for a service, and some of its parameters are other components, those are mocked:
```kotlin
private val systemToTest = CleanupService(
    dao = run {
        val ret = mockk<CleanupDao>()
        // mock methods manually if needed
        ret
    })
```

**Note:** by default, all generated tests are disabled: `.config(enabled = false)`. The reason: we don't have to fix them all at once. 
**Note:** if a function returns an instance of a data class, generated test includes two possible ways to assert, as follows:
```kotlin
        "apple works".config(enabled = false) {
            val actual = systemToTest.apple(
                weight = BigDecimal("42")
            )
// Keep either these assertions
            assertSoftly {
                actual.name shouldBe "Whatever"
                actual.weight shouldBe BigDecimal("42")
            }
// or these assertions
            val expected = MyThing(
                name = "Whatever",
                weight = BigDecimal("42")
            )
            assertSoftly {
                actual.name shouldBe expected.name
                actual.weight shouldBe expected.weight
            }
        }
```
Generally, we only keep one of these two assertions. But in different situations we may want two different formats, so we just generate both.

Likewise, generated mocks have every public method and property mocked:

```kotlin
// all necessary imports

object MockkThingFactory {
    fun get(): ThingFactory {
        val ret = mockk<ThingFactory>()

        every { ret.myProperty } returns
                42

        (snip)
```
Every method with parameters is mocked twice, with specific parameter values (such as `weight = BigDecimal("42")`) and with generic `any()` (such as `weight = any()`):
```kotlin
        every {
            ret.apple(
                weight = any()
            )
        } returns
                MyThing(
                    name = "Whatever",
                    weight = BigDecimal("42")
                )

        every {
            ret.apple(
                weight = BigDecimal("42")
            )
        } returns
                MyThing(
                    name = "Whatever",
                    weight = BigDecimal("42")
                )
```
Depending on the situation, we may keep one of them or both.

**Note:** if we only want tests, we can run the following:
```kotlin
generateAllTests(
    ThingFactory::class,
    fileName = "src/test/kotlin/generated/ThingFactoryTest.kt",
)
```
Likewise, we can generate only mocks:
```kotlin
generateAllMockks(
    ThingFactory::class,
    fileName = "src/test/kotlin/generated/MockkThingFactory.kt",
)
```

[Complete example in ReadmeExample1Kotest](src/test/kotlin/unit/com/tgt/trans/dmo/common/examples/ReadmeExample1Kotest.kt)


## Parameterized tests

Creating parameterized tests can take significant time, much of which can be automated away.

## Generation

The following code generates parameterized tests:
```kotlin
            val testCases: List<List<Any?>> = testCases()
            generateParamsKotest(
                "src/test/kotlin/unit/generated/GeneratedParamsTestHappyPath.kt",
                instanceToTest = MyCalendar(setOf()),
                callableToTest = MyCalendar::advance,
                params = testCases
            )
```
These tests look as follows:
```kotlin
        "parameterized test" {
            listOf(
                row(LocalDate.of(2022, 2, 7), 1, LocalDate.of(2022, 2, 8), "Add Clue"),
                row(LocalDate.of(2022, 2, 7), 2, LocalDate.of(2022, 2, 9), "Add Clue"),
// snip
            ).forAll { (date,
                           days,
                           expectedOutcome,
                           clue) ->
                withClue(clue) {
                    systemToTest.advance(
                        date,
                        days
                    ) shouldBe expectedOutcome
                }
            }
        }
```
[Example 11 in this file](src/test/kotlin/unit/com/tgt/trans/dmo/common/examples/ReadmeExamplesKotest.kt)


## Dealing with lists, sets, and maps.
### Generating sample lists, sets, and maps.
When Kotlin is compiled, element types of collections are erased. For example, suppose that the following class definition is compiled:
```kotlin
data class ThingWithListAndSet(
    val name: String,
    val createdAt: LocalDateTime,
    val attributes: Set<MyThing>,
    val importantDates: List<LocalDate>
)
```
When we reflect it, we know that `attributes` is a `Set`, but we do not know the type of its elements.
As such, whenever we are generating sample values and encounter a field or a parameter that is a collection, the only sample value we can generate is an empty one: `listOf()`, `setOf()`, or `mapOf()`:
```kotlin
val actual = systemToTest.mergeWith(
    other = ThingWithListAndSet(
        //(snip)
        attributes = setOf(),
        importantDates = listOf()
    )
)
assertSoftly {
    //(snip)
    actual.attributes shouldBe setOf()
    actual.importantDates shouldBe listOf()
}
```
[Example 3 in this file](src/test/kotlin/unit/com/tgt/trans/dmo/common/examples/ReadmeExamplesKotest.kt)
If we need to pass non-empty collections as parameteres, we can generate sample instances separately, like this:
```kotlin
generateSampleInstances(
    "src/test/kotlin/unit/generated/Example3b.kt",
    MyThing::class,
    LocalDate::class
)
```
[Example 3 in this file](src/test/kotlin/unit/com/tgt/trans/dmo/common/examples/ReadmeExamplesKotest.kt)
Then we have to add these elements manually - this is the best we can do at this time.

### Serializing actual lists, sets, and maps.
All the elements of actual lists, sets, and maps are serialized just like individual actual instances.
For example, if we serialize the following instance:
```kotlin
val instance = SampleCollections(
    name = "Example",
    myList = listOf("Amber", LocalTime.of(12, 34, 56)),
    (snip)
)
```
the field `myList` will be serialized as follows:
```kotlin
myList = listOf(
    "Amber",
    LocalTime.of(12, 34, 56, 0)
)

actual.myList shouldBe listOf(
    "Amber",
    LocalTime.of(12, 34, 56, 0)
)

every { ret.myList } returns listOf(
    "Amber",
    LocalTime.of(12, 34, 56, 0)
)
```
[Example 10 in this file](src/test/kotlin/unit/com/tgt/trans/dmo/common/examples/ReadmeExamplesKotest.kt)

## Customizing 

### Custom sample values
Suppose we want to change the default sample value for `Byte`. The following code uses today's day of month instead of the default:
```kotlin
val customSerializer = ExactClassSampleValueVisitor(
    Byte::class,
    listOf(Byte::class.qualifiedName!!, java.time.LocalDate::class.qualifiedName!!)
) {
    LocalDate.now().dayOfMonth.toString()
}
```
and this is how we plug it in:
```kotlin
val customFactory = SampleInstanceFactory(
    customVisitors =  listOf(customSerializer)
)
customFactory.generateAllKotests(
    "src/test/kotlin/unit/generated/Example4.kt",
    WithByte::class
)
```
[Example 4 in this file](src/test/kotlin/unit/com/tgt/trans/dmo/common/examples/ReadmeExamplesKotest.kt)

### Custom serializing of actual values
Suppose we want to change how `LocalDateTime` is serialized. By default, the output uses factory method `LocalDateTime.of`, like this:
`LocalDateTime.of(2021, 12, 28, 1, 2, 3, 4)`
Suppose we would rather use nested factory methods, like this:
```
LocalDateTime.of(
    LocalDate.of(2021, 12, 28),
    LocalTime.of(1, 2, 3, 4)
)
```

The following code defines how to serialize `LocalDateTime` and which classes to import:
```kotlin
val customLocalDateTimeSerializer = ExactClassSerializer(
    LocalDateTime::class,
    classesToImport = listOf(
        LocalDateTime::class,
        LocalDate::class,
        LocalTime::class
    )  { value: Any ->
      with(value as LocalDateTime) {
          "LocalDateTime.of(\nLocalDate.of(${year}, ${monthValue}, ${dayOfMonth}),\nLocalTime.of(${hour}, ${minute}, ${second}, ${nano})\n)"
      }
  }
```
So let us plug it in:
```kotlin
val customizedFactory = ActualInstanceFactory(
    customSerializers = listOf(customLocalDateTimeSerializer)
)
customizedFactory.serializeToKotlin(
    "src/test/kotlin/unit/generated/Example5.kt",
    LocalDateTime.of(2021, 12, 28, 1, 2, 3, 4)
)
```
and get the output we want.
[Example 5 in this file](src/test/kotlin/unit/com/tgt/trans/dmo/common/examples/ReadmeExamplesKotest.kt)


## How it works

This library uses reflection, so it can generate tests/mocks for a class as soon as it compiles. It does not parse source code.

The algorithm to provide sample values considering the type of fields/parameters is as follows

* if there is a custom serializer for the class, use it
* if it is a basic type for which we have a default serializer, use it
* if it is an enum, provide its first value as a sample, for example `DayOfWeek.MONDAY`
* if it is a list, a set, or a map, provide an empty `listOf(), setOf(), mapOf()` ,
* if it is a class with a public constructor, generate code to invoke that constructor, recursively handling its fields. A public primary constructor is considered first, before other ones.
* otherwise just mock it

The algorithm to serialize actual instances is very similar, but we use actual values for all fields, and serialize all the elements of collections, rather than provide empty ones.

### Classes supported by default:

For sample values:

```kotlin
DefaultSampleValueVisitor().supportedClasses().forEach {
    println(it)
}
```

For actual values:
```kotlin
DefaultClassesSerializerFactory.supportedClasses
    .map { it.qualifiedName!! }
    .sorted()
    .forEach {
        println(it)
    }
```

### If a field is a data class, we recursively analyze its fields
Because `myThing` field is a data class itself, we analyze its fields, and generate output like the following:
```kotlin
thing = NestedThing(
    name = "Whatever",
    myThing = MyThing(
        name = "Whatever",
        weight = BigDecimal("41")
    )
)
```

### If none of the above conditions matches, we generate a mock
In the following example `anotherService` class does not have a public primary contructor, so it is mocked:
```kotlin
private val systemToTest = MyService(
    anotherService =  run {
        val ret = mockk<AnotherService>()
        // mock methods manually if needed
        ret
    }
)
```
We can also provide a custom serializer for that class, as was described above.

### Limitations / TODO list

The following cases are not supported at this time:

* suspend functions
* varargs
* functions in Companion objects
* functions in inner classes
* top level functions
* generics
* arrays

