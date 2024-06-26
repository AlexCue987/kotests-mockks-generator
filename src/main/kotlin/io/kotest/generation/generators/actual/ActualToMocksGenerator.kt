package io.kotest.generation.generators.actual

import io.kotest.generation.actual.ActualInstanceFactory
import io.kotest.generation.actual.SerializeToMockValuesStrategy
import io.kotest.generation.common.*
import java.io.File

fun serializeToMocks(vararg instances: Any
) = serializeToMocksWithCustomization(
    ActualInstanceFactory(),
    "MockkFrom${simpleNameOfFirstInstance(*instances)}.kt",
    *instances
)

fun serializeToMocks(filename: String,
                     vararg instances: Any
) = serializeToMocksWithCustomization(
    ActualInstanceFactory(),
    filename,
    *instances
)

fun ActualInstanceFactory.serializeToMocks(filename: String,
                                            vararg instances: Any
) = serializeToMocksWithCustomization(
    this,
    filename,
    *instances
)

private fun serializeToMocksWithCustomization(
    factory: ActualInstanceFactory,
    filename: String,
    vararg instances: Any
) {
    val MocksFactory = ActualInstanceFactory(
        factory.customSerializers,
        SerializeToMockValuesStrategy
    )
    val serializedInstances = instances.map {
        when {
            isInstanceOfDataClass(it) ->         TypedCode(
                it::class.simpleName!!,
                it::class.qualifiedName!!,
                MocksFactory.serializeInstance(it)
            )
            else -> TypedCode(
                "Any",
                "kotlin.Any",
                CodeSnippet.oneLiner("//Cannot serialize ${it::class.qualifiedName} because not a data class")
            )
        }
    }
    val classesToImport = ImportsGenerator().generate(serializedInstances.map { it.code })
    val code = """package generated.code

import io.mockk.every
import io.mockk.mockk
${classesToImport.joinToString("\n") {"import $it" }}

// generated by io.kotest.generation:kotests-generator
object SerializedMocks {
${serializedInstances.mapIndexed { 
            index, instance -> 
            "fun mock$index(): ${instance.simpleName} {\n" +
            "val ret = mockk<${instance.simpleName}>()\n" +
            "${instance.code.sourceCodeAsOneString()}\nreturn ret\n}" }.joinToString("\n\n")}
}
        """.trimIndent()
    File(filename).writeText(code)
}

