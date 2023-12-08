package io.kotest.generation.generators.common

typealias NameValueFormatter = (String, String, Boolean) -> String

fun parameterAssignment(
    name: String,
    value: String,
    isLast: Boolean
): String {
    return "$name = $value${if(isLast) "" else ","}"
}

fun assertionForField(
    name: String,
    value: String,
    isLast: Boolean
): String {
    return "actual.$name shouldBe $value"
}

fun assertionForInstance(
    name: String,
    value: String,
    isLast: Boolean
): String {
    return "actual shouldBe $value"
}

fun onlyValue(
    name: String,
    value: String,
    isLast: Boolean
): String {
    return "$value${if(isLast) "" else ","}"
}
