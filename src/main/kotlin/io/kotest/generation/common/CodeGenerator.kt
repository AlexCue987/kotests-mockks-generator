package io.kotest.generation.common

import io.kotest.generation.kotest.KotestData

interface CodeGenerator {
    fun generateCode(data: KotestData): String
}