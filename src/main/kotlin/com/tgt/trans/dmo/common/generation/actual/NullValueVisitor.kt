package com.tgt.trans.dmo.common.generation.actual

import com.tgt.trans.dmo.common.generation.common.CodeSnippet

class NullValueVisitor() : SerializerVisitor {
    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addLine("null", isLast)
    }

    override fun canHandle(instance: Any?) = (instance == null)
}