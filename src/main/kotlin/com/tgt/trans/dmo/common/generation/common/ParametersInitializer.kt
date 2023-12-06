package com.tgt.trans.dmo.common.generation.common

import com.tgt.trans.dmo.common.generation.kotest.NameValueFormatter
import kotlin.reflect.KParameter

interface ParametersInitializer {
    fun initializeParameters(
        parameters: List<KParameter>,
        ret: CodeSnippet,
        nameValueFormatter: NameValueFormatter
    )
}