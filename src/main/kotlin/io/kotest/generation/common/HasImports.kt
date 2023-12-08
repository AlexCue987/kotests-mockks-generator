package io.kotest.generation.common

interface HasImports{
    fun qualifiedNames(): Collection<String>
}

interface HasSourceCode: HasImports {
    fun sourceCode(): Collection<String>
    fun sourceCodeAsOneString(separator: String = "\n") = sourceCode().joinToString(separator)
}