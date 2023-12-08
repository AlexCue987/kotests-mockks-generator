package io.kotest.generation.common

import io.kotest.generation.sample.SampleInstanceFactory
import kotlin.reflect.KParameter

class ParametersLoopVisitor(
    private val factory: SampleInstanceFactory
    ) {
    private fun addParameters(
        parameters: List<KParameter>,
        buffer: CodeSnippet,
        nameValueFormatter: (name: String) -> String,
        ignoreCommas: Boolean
    ) {
        parameters.forEachIndexed { index, parameter ->
            val lastParameter = (index == parameters.size - 1)
            addParameter(parameter, buffer, lastParameter || ignoreCommas, nameValueFormatter)
        }
    }

    fun addParameterAssignments(
        parameters: List<KParameter>,
        buffer: CodeSnippet
    ) = addParameters(parameters, buffer, ::parameterAssignment, ignoreCommas = false)

    fun addAssertions(
        parameters: List<KParameter>,
        buffer: CodeSnippet
    ) = addParameters(parameters, buffer, ::assertion, ignoreCommas = true)

    fun addParameter(
        parameter: KParameter,
        buffer: CodeSnippet,
        isLast: Boolean,
        nameValueFormatter: (name: String) -> String
    ) {
        val klass = kParameterToKClass(parameter)
        val parameterName = parameter.name!!
        buffer.addText(nameValueFormatter(parameterName))
        factory.addValue(klass, buffer, isLast)
    }

    companion object {
        private fun parameterAssignment(parameterName: String) = "$parameterName = "
        private fun assertion(parameterName: String) = "expected.$parameterName shouldBe "
    }
}