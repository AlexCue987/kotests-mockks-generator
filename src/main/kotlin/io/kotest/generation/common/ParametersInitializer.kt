package io.kotest.generation.common

import io.kotest.generation.kotest.NameValueFormatter
import kotlin.reflect.KParameter

interface ParametersInitializer {
    fun initializeParameters(
        parameters: List<KParameter>,
        ret: CodeSnippet,
        nameValueFormatter: NameValueFormatter
    )
}