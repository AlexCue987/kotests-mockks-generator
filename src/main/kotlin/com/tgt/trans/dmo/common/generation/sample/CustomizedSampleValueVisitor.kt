package com.tgt.trans.dmo.common.generation.sample

import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import kotlin.reflect.KClass

class CustomizedSampleValueVisitor(
    private val visitors: List<SampleSerializerVisitor>
): SampleSerializerVisitor {
    override fun canHandle(klass: KClass<*>): Boolean  = visitors.any { it.canHandle(klass) }

    override fun handle(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        visitors.any {
            it.serialize(klass, buffer, isLast)
        }
    }
}