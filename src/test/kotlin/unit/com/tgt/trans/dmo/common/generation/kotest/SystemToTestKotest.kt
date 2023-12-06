package com.tgt.trans.dmo.common.generation.kotest

import com.tgt.trans.dmo.common.generation.ThingFactory
import com.tgt.trans.dmo.common.generation.common.CodeSnippetFactory
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class SystemToTestKotest: StringSpec() {
    private val qualifiedName = "com.tgt.trans.dmo.common.generation.ThingFactory"
    init {
        "provides class names" {
            val actual = SystemToTest(ThingFactory::class, CodeSnippetFactory())
            assertSoftly {
                actual.simpleName shouldBe "ThingFactory"
                actual.qualifiedName shouldBe qualifiedName
            }
        }

        "provides class name to classesToImport" {
            val actual = SystemToTest(ThingFactory::class, CodeSnippetFactory())
            actual.qualifiedNames().toSet() shouldContainExactlyInAnyOrder listOf(qualifiedName, "kotlin.Int", "kotlin.String")
        }
    }
}