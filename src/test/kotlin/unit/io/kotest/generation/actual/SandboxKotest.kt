package io.kotest.generation.actual

import io.kotest.core.spec.style.StringSpec

class SandboxKotest: StringSpec() {
    init {
        "any" {
            val visitors = (0..5).asSequence().map { MyVisitor(it) }.toList()
            println(visitors.any { it.handle(7)})
        }
    }
}

class MyVisitor(val i: Int) {
    fun handle(value: Int) =
         (value == i).also { println("Visitor $i handling $value") }
}