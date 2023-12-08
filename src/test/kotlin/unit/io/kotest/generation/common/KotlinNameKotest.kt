package io.kotest.generation.common

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class KotlinNameKotest: StringSpec() {
    init {
        "nameNeedsBackticks" {
            listOf(
                row("validName", false),
                row("1startsWithNumber", true),
                row(" ", true),
                row("has-dash", true),
            ).forAll {
                    (name, expected) ->
                nameNeedsBackticks(name) shouldBe expected
            }
        }

        "toKotlinName" {
            listOf(
                row("validName", "validName"),
                row("1startsWithNumber", "`1startsWithNumber`"),
                row("has-dash", "`has-dash`"),
            ).forAll {
                    (name, expected) ->
                name.toKotlinName() shouldBe expected
            }
        }

    }
}