package io.kotest.generation.sample

import io.kotest.generation.common.CodeSnippet
import io.kotest.generation.common.CodeSnippetFactory
import io.kotest.generation.mockk.DefaultMockkGenerator
import io.kotest.generation.mockk.MockkDataFactory
import kotlin.reflect.KClass

class LastResortSimpleMockVisitor(
    private val codeSnippetFactory: CodeSnippetFactory = CodeSnippetFactory()
): SampleSerializerVisitor {
    override fun canHandle(klass: KClass<*>): Boolean = true

    override fun handle(klass: KClass<*>, buffer: CodeSnippet, isLast: Boolean) {
        buffer.addClassName("io.mockk.mockk")
        val mockkData = MockkDataFactory().get(
            codeSnippetFactory,
            klass
        )
        buffer.addClassNames(mockkData.qualifiedNames())
        val mockkedParameter = DefaultMockkGenerator.generateAsValue(mockkData)
        buffer.add(mockkedParameter, addCommaAfterLastLine = !isLast)
    }
}