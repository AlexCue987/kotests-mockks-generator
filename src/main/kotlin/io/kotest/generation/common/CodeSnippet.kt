package io.kotest.generation.common

class CodeSnippet: HasSourceCode {
    private val codeLines = LineBuffer()
    private val classNames: MutableSet<String> = mutableSetOf()
    private var currentLinePrefix: String = ""

    fun addClassName(className: String) = classNames.add(className)

    fun addClassNames(classNamesToAdd: Collection<String>) = classNamesToAdd.forEach { classNames.add(it) }

    fun addLine(codeLine: String, suppressTerminator: Boolean = true) {
        codeLines.addLine("$currentLinePrefix$codeLine${lineTerminator(suppressTerminator)}")
        currentLinePrefix = ""
    }

    fun addText(text: String) {
        currentLinePrefix = "$currentLinePrefix$text"
    }

    override fun qualifiedNames() = classNames

    fun add(other: HasSourceCode,
            sourceCodePrefix: String = "",
            addCommaAfterLastLine: Boolean = false
    ) {
        classNames.addAll(other.qualifiedNames())
        other.sourceCode().forEachIndexed { index, line ->
            val lastLine = (index == other.sourceCode().size - 1)
            addLine("$sourceCodePrefix$line", suppressTerminator = !(addCommaAfterLastLine && lastLine))
        }
    }

    override fun sourceCode(): List<String> = codeLines.allLines()

    fun toOneLine(): CodeSnippet {
        val ret = CodeSnippet()
        ret.addClassNames(qualifiedNames())
        ret.addLine(codeLines.allLines().joinToString(" "))
        return ret
    }

    companion object {
        fun prefixedLines(prefix: String, lines: List<String>): List<String> {
            return lines.mapIndexed { index, line ->  "${if(index == 0) prefix else ""}$line"}
        }

        fun oneLiner(line: String): CodeSnippet {
            val ret = CodeSnippet()
            ret.addLine(line)
            return ret
        }

        fun of(line: String, qualifiedName: String): CodeSnippet {
            val buffer = CodeSnippet()
            buffer.addClassName(qualifiedName)
            buffer.addLine(line)
            return buffer
        }
    }
}

