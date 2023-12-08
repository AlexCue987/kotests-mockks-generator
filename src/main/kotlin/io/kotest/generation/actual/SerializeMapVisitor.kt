package io.kotest.generation.actual

import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.common.isMap
import io.kotest.generation.common.lineTerminator

class SerializeMapVisitor(
    private val instanceFactory: ActualInstanceFactory
    ): SerializerVisitor {

    override fun canHandle(instance: Any?): Boolean = instance?.let { isMap(instance) } ?: false

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        val map = instance!! as Map<*, *>
        if(map.isEmpty()) {
            buffer.addLine("mapOf()${lineTerminator(isLast)}")
            return
        }
        buffer.addLine("mapOf(")
        map.entries.forEachIndexed { index, entry ->
            val isLastEntry = (index == map.entries.size - 1)
            instanceFactory.addValue(entry.key, buffer, true)
            buffer.addLine("to")
            instanceFactory.addValue(entry.value, buffer, isLastEntry)
        }
        buffer.addLine(")${lineTerminator(isLast)}")
    }
}


