package io.kotest.generation.common

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ClassNameKotest: StringSpec() {
    init {
        "handle file name with extension" {
            className("src/test/kotlin/unit/com/tgt/trans/dmo/common/generation/GeneratedTest4ServiceWithCustomValues.kt",) shouldBe "GeneratedTest4ServiceWithCustomValues"
        }

        "handle file name without extension" {
            className("src/test/kotlin/unit/com/tgt/trans/dmo/common/generation/GeneratedTest4ServiceWithCustomValues",) shouldBe "GeneratedTest4ServiceWithCustomValues"
        }

        "handle file name without path" {
            className("GeneratedTest4ServiceWithCustomValues.kt",) shouldBe "GeneratedTest4ServiceWithCustomValues"
        }

        "handle file name without path or extention" {
            className("GeneratedTest4ServiceWithCustomValues",) shouldBe "GeneratedTest4ServiceWithCustomValues"
        }
    }
}