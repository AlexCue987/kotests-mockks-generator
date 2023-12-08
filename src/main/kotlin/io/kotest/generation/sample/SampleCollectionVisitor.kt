package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass

class SampleCollectionVisitor: SampleSerializerVisitor {
    override fun canHandle(klass: KClass<*>): Boolean =
        isList(klass) || isSet(klass) || isMap(klass)

    override fun handle(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        val snippet = when {
            isList(klass) -> sampleList
            isSet(klass) -> sampleSet
            isMap(klass) -> sampleMap
            else -> throw IllegalArgumentException("Not a list, map, or set: ${klass.qualifiedName}")
        }
        buffer.addClassNames(snippet.qualifiedNames())
        buffer.addLine(snippet.sourceCode()[0], isLast)
    }

    companion object {
        fun isList(klass: KClass<*>) = List::class.java.isAssignableFrom(klass.java)

        fun isSet(klass: KClass<*>) =  Set::class.java.isAssignableFrom(klass.java)

        fun isMap(klass: KClass<*>) =  Map::class.java.isAssignableFrom(klass.java)

        private val sampleList = CodeSnippet.of("listOf()", List::class.qualifiedName!!)
        private val sampleSet = CodeSnippet.of("setOf()", Set::class.qualifiedName!!)
        private val sampleMap = CodeSnippet.of("mapOf()", Map::class.qualifiedName!!)
    }
}