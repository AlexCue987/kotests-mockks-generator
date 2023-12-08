package io.kotest.generation.common

class ImportsGenerator {
    fun generate(vararg items: HasImports): List<String> =
        items.asSequence()
            .map { it.qualifiedNames() }
            .flatten()
            .sorted()
            .distinct()
            .toList()

    fun generate(items: List<HasImports>) = generate(*(items.toTypedArray()))
}