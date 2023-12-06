package com.tgt.trans.dmo.common.generation.common

import com.tgt.trans.dmo.common.generation.kotest.KotestData

interface CodeGenerator {
    fun generateCode(data: KotestData): String
}