package io.kotest.generation.common

class LineBuffer {
    private val lines: MutableList<String> = mutableListOf()

    fun addLine(line: String) = lines.add(line)

    fun addLines(linesToAdd: Collection<String>) = lines.addAll(linesToAdd)

    fun allLines(): List<String> = lines
}
