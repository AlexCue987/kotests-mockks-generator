package com.tgt.trans.dmo.common.generation.sample

import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import com.tgt.trans.dmo.common.generation.common.firstEnumValue
import com.tgt.trans.dmo.common.generation.common.isEnum
import kotlin.reflect.KClass

class EnumSampleValueVisitor: SampleSerializerVisitor {
    override fun canHandle(klass: KClass<*>): Boolean = isEnum(klass)

    override fun handle(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addClassName(klass.qualifiedName!!)
        buffer.addLine("${klass.simpleName}.${firstEnumValue(klass)}", isLast)
    }
}