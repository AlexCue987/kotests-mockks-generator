package com.tgt.trans.dmo.common.generation.kotest

import com.tgt.trans.dmo.common.generation.ThingFactory
import com.tgt.trans.dmo.common.generation.common.CodeSnippetFactory
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class KotestDataFactoryKotest: StringSpec() {
    private val systemToTest = KotestDataFactory()
    private val qualifiedName = "com.tgt.trans.dmo.common.generation.ThingFactory"

    init {
        "provides class names" {
            val actual = systemToTest.get(CodeSnippetFactory(), ThingFactory::class)
            assertSoftly {
                actual.simpleName shouldBe "ThingFactory"
                actual.qualifiedName shouldBe qualifiedName
            }
        }

        "provides class name to classesToImport" {
            val actual = systemToTest.get(CodeSnippetFactory(), ThingFactory::class)
            actual.classesToImport shouldBe listOf(qualifiedName, "kotlin.Int", "kotlin.String")
        }

        "includes classes method" {

        }
    }
}
