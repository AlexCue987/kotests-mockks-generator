package io.kotest.generation.actual

import io.kotest.generation.common.*

class SerializeCollectionVisitor(
    private val instanceFactory: ActualInstanceFactory
): SerializerVisitor {
    override fun canHandle(instance: Any?): Boolean = instance?.let { isListOrSet(instance) } ?: false

    override fun handle(instance: Any?, buffer: CodeSnippet, isLast: Boolean) {
        val collection = instance!! as Collection<*>
        if(collection.isEmpty()) {
            buffer.addLine("${getCollectionFactoryName(collection)}Of()${lineTerminator(isLast)}")
            return
        }
        buffer.addLine("${getCollectionFactoryName(collection)}Of(")
        collection.forEachIndexed { index, item ->
            val isLastEntry = (index == collection.size - 1)
            instanceFactory.addValue(item, buffer, isLastEntry)
        }
        buffer.addLine(")${lineTerminator(isLast)}")
    }

    fun getCollectionFactoryName(collection: Collection<*>): String {
        return when {
            isList(collection) -> "list"
            isSet(collection) -> "set"
            else -> throw IllegalArgumentException("Not a List or a Set: ${collection::class.qualifiedName}")
        }
    }
}