package io.kotest.generation.actual

import io.kotest.generation.generators.actual.serializeToAssertions
import io.kotest.generation.generators.actual.serializeToKotlin
import io.kotest.generation.generators.actual.serializeToMocks
import io.kotest.generation.generators.actual.serializeToParameterizedMocks

fun serializeForTests(vararg instances: Any) {
    serializeToKotlin(*instances)
    serializeToAssertions(*instances)
    serializeToMocks(*instances)
    serializeToParameterizedMocks(*instances)
}