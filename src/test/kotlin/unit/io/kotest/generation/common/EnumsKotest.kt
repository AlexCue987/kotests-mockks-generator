package io.kotest.generation.common

import java.time.DayOfWeek
import kotlin.reflect.full.memberFunctions
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll

class EnumsKotest: StringSpec() {
    init {
        "enum" {
            val klass = DayOfWeek::class
            Class.forName(klass.qualifiedName).isEnum
            val enums = Class.forName(klass.qualifiedName).enumConstants
            enums.forAll {
                name ->
                println(name)
            }
            klass.memberFunctions.forAll {
                val name = it.name
                println(name)
            }
            val values = klass.memberFunctions.filter { it.name == "values" }
            println(values)
        }
    }
}