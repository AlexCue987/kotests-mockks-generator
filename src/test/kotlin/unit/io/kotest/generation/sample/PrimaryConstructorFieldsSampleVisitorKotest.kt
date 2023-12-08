package io.kotest.generation.sample

import io.kotest.generation.MyThing
import io.kotest.generation.MyThingWithPrivatPrimaryConstructor
import io.kotest.generation.ServiceNotADataClass
import io.kotest.generation.common.CodeSnippet
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class PrimaryConstructorFieldsSampleVisitorKotest: StringSpec() {
    override fun isolationMode() = IsolationMode.InstancePerTest

    private val sampleInstanceFactory = SampleInstanceFactory()
    private val sut = PrimaryConstructorFieldsSampleVisitor(sampleInstanceFactory)
    private val buffer = CodeSnippet()

    init {
        "canHandle" {
            assertSoftly {
                sut.canHandle(MyThing::class) shouldBe true
                withClue("not a data class, but has public primary constructor") {
                    sut.canHandle(ServiceNotADataClass::class) shouldBe true
                }
                withClue("primary constructor is private") {
                    sut.canHandle(MyThingWithPrivatPrimaryConstructor::class) shouldBe false
                }
            }
        }

        "handle" {
            sut.handle(MyThing::class, buffer, true)
//            buffer.sourceCode().forEach {
//                println("\"\"\"$it\"\"\",")
//            }
            assertSoftly {
                buffer.sourceCode() shouldBe listOf(
                    """MyThing(""",
                    """name = "Whatever",""",
                    """weight = BigDecimal("42")""",
                    """)""",
                    )
                buffer.qualifiedNames() shouldContainExactlyInAnyOrder  listOf(
                    "io.kotest.generation.MyThing",
                    "kotlin.String",
                    "java.math.BigDecimal"
                )
            }
        }
    }
}