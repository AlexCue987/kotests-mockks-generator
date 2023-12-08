package io.kotest.generation.mockk

import io.kotest.generation.AnotherService
import io.kotest.generation.ThingFactory
import io.kotest.generation.common.CodeSnippetFactory
import io.kotest.generation.common.kTypeToKClass
import io.kotest.core.spec.style.StringSpec
import java.math.BigDecimal
import java.math.BigInteger

class MockkDataFactoryKotest: StringSpec() {
    private val customCodeSnippetFactory = CodeSnippetFactory
        .withAdditionalSampleValues(
            BigDecimal::class to { "BigDecimal(\"41\")" },
            BigInteger::class to { "BigInteger.valueOf(31L)" }
        )

    init {
        "generate mockk" {
//            customCodeSnippetFactory.generateMockks(
//                "src/test/kotlin/unit/com/tgt/trans/dmo/common/generation/GeneratedMockk.kt",
//                AnotherService::class,
//                AnotherService::doSomething,
//                AnotherService::doSomethingElse,
//                AnotherService::voidFun,
//                AnotherService::doSeveralThings,
//                AnotherService::getDate
//            )
        }

        "generate mockk for README.md".config(enabled = false) {
            generateAllMockks(
                ThingFactory::class,
                "src/test/kotlin/unit/com/tgt/trans/dmo/common/generation/GeneratedMockk4README.kt",
            )
        }

        "what types are supported" {
            CodeSnippetFactory().supportedClasses.sortedBy { it.toString() } .forEach { println(it) }
        }
    }
}