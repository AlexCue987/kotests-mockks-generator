package io.kotest.generation.generators.parameterized

import io.kotest.generation.actual.ActualInstanceFactory
import io.kotest.generation.common.*
import io.kotest.generation.generators.common.InstanceCodeSnippet
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KCallable
import kotlin.reflect.KProperty

class ParamsKotestFactory(
    val codeSnippetFactory: ActualInstanceFactory = ActualInstanceFactory()
) {
   fun generateParamsKotest(
        instance: Any,
        callableToTest: KCallable<*>,
        params: List<List<Any?>>
    ): ParamsKotest {
        val systemToTest = codeSnippetFactory.serializeInstance(instance)
        val rows = params.map { rowOf(instance, callableToTest, it) }
        val hasExceptions = rows.any { row -> row.last()!! is CallException }
        val rowSnippets = rows.map { if(hasExceptions)
            rowSnippetWithCompleteOutcome(it) else
            rowSnippetWithSuccessOnly(it)
        }
        return ParamsKotest(
            InstanceCodeSnippet(instance::class, systemToTest),
            callableToTest.name,
            parameters(callableToTest),
            rowSnippets,
            hasExceptions
        )
    }

    fun rowSnippetWithCompleteOutcome(row: List<Any?>): CodeSnippet {
        val buffer = CodeSnippet()
        buffer.addLine("row(")
        row.forEach { value ->
            codeSnippetFactory.addValue(value, buffer, isLast = false)
        }
        buffer.addLine("\"Add Clue\")")
        return buffer.toOneLine()
    }

    fun rowSnippetWithSuccessOnly(row: List<Any?>): CodeSnippet {
        val buffer = CodeSnippet()
        buffer.addLine("row(")
        row.forEachIndexed { index, value ->
            val valueToInclude = if (isLastIndex(index, row)) (value as CallResult).result else value
            codeSnippetFactory.addValue(valueToInclude, buffer, isLast = false)
        }
        buffer.addLine("\"Add Clue\")")
        return buffer.toOneLine()
    }

    fun parameters(callableToTest: KCallable<*>): String = when(callableToTest) {
        is KProperty -> ""
        else -> methodParameters(callableToTest).joinToString(",\n") { it.name!! }
    }

    fun rowOf(
        instance: Any,
        callableToTest: KCallable<*>,
        params: List<Any?>
    ): List<Any?> {
        val callOutcome = try {
            CallResult(callableToTest.call(instance, *(params.toTypedArray())))
        } catch (ex: Throwable) {
            if(ex is InvocationTargetException) {
                callException(ex.targetException!!)
            } else {
                CallException(ex)
            }
        }
        return params + callOutcome
    }
}