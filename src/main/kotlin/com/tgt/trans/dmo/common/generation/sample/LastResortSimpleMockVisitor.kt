package com.tgt.trans.dmo.common.generation.sample

import com.tgt.trans.dmo.common.generation.common.CodeSnippet
import com.tgt.trans.dmo.common.generation.common.CodeSnippetFactory
import com.tgt.trans.dmo.common.generation.mockk.DefaultMockkGenerator
import com.tgt.trans.dmo.common.generation.mockk.MockkDataFactory
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