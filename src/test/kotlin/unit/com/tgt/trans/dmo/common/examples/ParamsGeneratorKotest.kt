package com.tgt.trans.dmo.common.examples

import com.tgt.trans.dmo.common.generation.generators.parameterized.generateParamsKotest
import io.kotest.core.spec.style.StringSpec
import java.time.LocalDate

class ParamsGeneratorKotest: StringSpec() {
    init {
        "generate params for happy path".config(enabled = false) {
            val testCases = myTestCases()
            testCases.forEach { println(it) }
            generateParamsKotest(
                "src/test/kotlin/generated/GeneratedParamsTestHappyPath.kt",
                instanceToTest = MyCalendar(setOf()),
                callableToTest = MyCalendar::advance,
                params = testCases
            )
        }

        "generate params for exceptions".config(enabled = false) {
            val rows = listOf(
                listOf(LocalDate.of(2022, 2, 7), 1),
                listOf(LocalDate.of(2022, 2, 7), 2),
                listOf(LocalDate.of(2022, 2, 8), 1),
            )
            generateParamsKotest(
                "src/test/kotlin/unit/generated/GeneratedParamsTestWithExceptions.kt",
                instanceToTest = MyCalendar(setOf(LocalDate.of(2022, 2, 8))),
                callableToTest = MyCalendar::advance,
                params = rows
            )
        }
    }

    fun myTestCases(): List<List<Any?>> {
        val startDays = (7..13).asSequence()
        val rows = startDays.map {
            val date = LocalDate.of(2022, 2, it)
            listOf(listOf(date, 1), listOf(date, 2))
        }.flatten().toList()
        return rows
    }
}

data class MyCalendar(
    val holidays: Set<LocalDate>
) {
    fun advance(date: LocalDate, daysToAdvance: Int): LocalDate = when (date) {
        in holidays -> throw IllegalArgumentException("Date cannot be holiday: $date")
        else -> date.plusDays(daysToAdvance.toLong())
    }
}