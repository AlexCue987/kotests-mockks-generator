package com.tgt.trans.dmo.common.generation.actual

import com.tgt.trans.dmo.common.generation.common.CodeSnippet

interface SerializerVisitor {
    fun serialize(
        instance: Any?,
        buffer: CodeSnippet,
        isLast: Boolean
    ): Boolean {
        if(canHandle((instance))) {
            handle(instance, buffer, isLast)
            return true
        }
        return false
    }

    fun canHandle(instance: Any?): Boolean

    fun handle(
        instance: Any?,
        buffer: CodeSnippet,
        isLast: Boolean
    )
}