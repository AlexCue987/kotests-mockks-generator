package com.tgt.trans.dmo.common.generation.actual

import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import com.tgt.trans.dmo.common.generation.common.isEnum
import com.tgt.trans.dmo.common.generation.common.lineTerminator
import com.tgt.trans.dmo.common.generation.common.toKotlinName

class EnumVisitor: SerializerVisitor {
    override fun canHandle(instance: Any?): Boolean = instance?.let { isEnum(it::class) } ?: false

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        val klass = instance!!::class
        val line = "${klass.simpleName}.${instance.toString().toKotlinName()}"
        buffer.addLine(line, isLast)
        buffer.addClassName(klass.qualifiedName!!)
    }
}