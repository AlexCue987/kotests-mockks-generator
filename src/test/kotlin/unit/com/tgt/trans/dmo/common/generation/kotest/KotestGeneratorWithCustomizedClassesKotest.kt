package com.tgt.trans.dmo.common.generation.kotest

import com.tgt.trans.dmo.common.generation.AnotherService
import com.tgt.trans.dmo.common.generation.ServiceWithLotsOfParameters
import com.tgt.trans.dmo.common.generation.ThingFactory
import com.tgt.trans.dmo.common.generation.common.CodeSnippetFactory
import io.kotest.core.spec.style.StringSpec
import java.math.BigDecimal
import java.math.BigInteger

class KotestGeneratorWithCustomizedClassesKotest: StringSpec() {
    private val customCodeSnippetFactory = CodeSnippetFactory
        .withAdditionalSampleValues(
            BigDecimal::class to { "BigDecimal(\"41\")" },
            BigInteger::class to { "BigInteger.valueOf(31L)" }
        )

    init {
        "generate tests".config(enabled = false) {
            customCodeSnippetFactory.generateKotests(
                "src/test/kotlin/generated/GeneratedTest4ServiceWithCustomValues.kt",
                AnotherService::class,
                AnotherService::doSomething,
                AnotherService::doSomethingElse,
                AnotherService::doSeveralThings,
                AnotherService::doOneMoreThing,
                AnotherService::voidFun,
                AnotherService::getDate
            )
        }
    }
}