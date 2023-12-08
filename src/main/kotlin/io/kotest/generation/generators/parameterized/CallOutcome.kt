package io.kotest.generation.generators.parameterized

sealed class CallOutcome {}

data class CallResult(val result: Any?): CallOutcome()

fun callResult(result: Any?): CallOutcome = CallResult(result)

data class CallException(val throwable: Throwable): CallOutcome()

fun callException(throwable: Throwable): CallOutcome = CallException(throwable)
