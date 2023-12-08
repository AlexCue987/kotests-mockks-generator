package io.kotest.generation.kotest

import io.kotest.generation.AnotherService
import io.kotest.generation.ServiceWithLotsOfParameters
import io.kotest.generation.ThingFactory
import io.kotest.core.spec.style.StringSpec

class KotestGeneratorKotest: StringSpec() {
    init {
        "generate tests" {
//            generateKotests(
//                "src/test/kotlin/unit/com/tgt/trans/dmo/common/generation/GeneratedTest.kt",
//                ThingFactory::class,
//                ThingFactory::apple,
//                ThingFactory::orange
//            )
        }

        "generate tests for service that returns collections" {
//            generateKotests(
//                "src/test/kotlin/unit/com/tgt/trans/dmo/common/generation/GeneratedTest4Collections.kt",
//                ServiceWithLotsOfParameters::class,
//                ServiceWithLotsOfParameters::transformToBigDecimal,
//                ServiceWithLotsOfParameters::transformToInt,
//                ServiceWithLotsOfParameters::transformToLocalDate,
//                ServiceWithLotsOfParameters::transformToInstance,
//                ServiceWithLotsOfParameters::transformToList,
//                ServiceWithLotsOfParameters::transformToMap,
//                ServiceWithLotsOfParameters::transformToSet
//            )
        }
    }
}