package com.tgt.trans.dmo.common.generation.sample

import com.tgt.trans.dmo.common.generation.common.*
import java.io.File
import kotlin.reflect.KClass

fun generateSampleInstances(filename: String,
                            vararg klasses: KClass<*>
) = generateSampleInstancesWithCustomClasses(
    CodeSnippetFactory(),
    filename,
    *klasses
)

fun CodeSnippetFactory.generateSampleInstances(filename: String,
                            vararg klasses: KClass<*>
) = generateSampleInstancesWithCustomClasses(
    this,
    filename,
    *klasses
)

private fun generateSampleInstancesWithCustomClasses(
    codeSnippetFactory: CodeSnippetFactory,
    filename: String,
    vararg klasses: KClass<*>
) {
    val instances = klasses.map { VariableAssignment(variableNameOf(it), sampleInstance(it, codeSnippetFactory)) }
    val classesToImport = ImportsGenerator().generate(instances.map { it.code })
    val code = """
${classesToImport.joinToString("\n") {"import $it" }}

// generated by com.tgt.trans.dmo.common:kotests-generator
class SampleData {
companion object {
${instances.map { instance -> "fun ${instance.name}() = ${instance.code.sourceCodeAsOneString()}" }.joinToString("\n\n")}
}
}
        """.trimIndent()
    File(filename).writeText(code)
}

data class VariableAssignment(val name: String, val code: CodeSnippet)