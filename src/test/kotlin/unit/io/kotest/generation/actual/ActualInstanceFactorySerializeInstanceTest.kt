package io.kotest.generation.actual

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldStartWith

class ActualInstanceFactorySerializeInstanceTest: StringSpec() {
    init {
        "handle infinite recursion" {
            val node = Node("Self-loop", null)
            node.parent = node
            val actual = ActualInstanceFactory().serializeInstance(node)
            actual.sourceCode() shouldStartWith
                    listOf(
                        "Node(",
                        "name = \"\"\"Self-loop\"\"\",",
                        "parent = Node(",
                        "name = \"\"\"Self-loop\"\"\",")
            actual.sourceCode() shouldContain "// cannot serialize deep recursion)"
        }
    }

    private data class Node(
        val name: String,
        var parent: Node?
    )
}