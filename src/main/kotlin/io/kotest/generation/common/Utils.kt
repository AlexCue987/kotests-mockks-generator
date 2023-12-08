package io.kotest.generation.common

import kotlin.reflect.*
import kotlin.reflect.jvm.jvmErasure

fun kTypeToKClass(type: KType): KClass<*> = type.jvmErasure

fun kParameterToKClass(parameter: KParameter): KClass<*> = parameter.type.jvmErasure

fun packageName(klass: KClass<*>): String {
    val tokens = klass.qualifiedName!!.split(".").toList()
    return tokens.subList(0, tokens.size - 1).joinToString(".")
}

fun methodParameters(method: KCallable<*>) =
    method.parameters
        .filter { it.name != null && !it.isVararg}

fun hasParameters(method: KCallable<*>) = methodParameters(method).isNotEmpty()

fun isVoid(method: KCallable<*>) = kTypeToKClass(method.returnType) == Unit::class

fun variableNameOf(klass: KClass<*>): String {
    val name = klass.simpleName!!
    val firstChar = name[0].toLowerCase()
    return "$firstChar${name.substring(1)}"
}

fun isDataClass(klass: KClass<*>): Boolean = klass.isData
fun isInstanceOfDataClass(instance: Any): Boolean = isDataClass(instance::class)

fun isEnum(klass: KClass<*>) = klass.java.isEnum

fun firstEnumValue(klass: KClass<*>): String {
    require(isEnum(klass)) { "Must be enum, was ${klass.simpleName}" }
    return klass.java.enumConstants.asSequence().map { it.toString() }.first()!!
}

fun lineTerminator(suppressTerminator: Boolean) = if(suppressTerminator) "" else ","

fun Any.isDescendantOf(klass: KClass<*>) = klass.java.isAssignableFrom(this::class.java)

fun isList(instance: Any) = instance.isDescendantOf(List::class)

fun isSet(instance: Any) = instance.isDescendantOf(Set::class)

fun isListOrSet(instance: Any) = isList(instance) || isSet(instance)

fun isMap(instance: Any) = instance.isDescendantOf(Map::class)

fun isCollection(instance: Any) = instance.isDescendantOf(Collection::class)

fun isLastIndex(index: Int, list: List<*>) = (index == (list.size - 1))

fun isProperty(callable: KCallable<*>) = callable is KProperty

fun isFunction(callable: KCallable<*>) = callable is KFunction

fun String.toKotlinName(): String = when {
    nameNeedsBackticks(this) -> "`$this`"
    else -> this
}

internal fun nameNeedsBackticks(name: String): Boolean = when {
    name.isEmpty() || name.isBlank() -> true
    !Character.isJavaIdentifierStart(name[0]) -> true
    else -> name.asSequence().any { !Character.isJavaIdentifierPart(it) }
}

fun className(fullPathFileName: String): String {
    val tokens = fullPathFileName.split("/")
    val fileName = tokens.last()
    return fileName.split(".")[0]
}

fun makeSureFolderExistsFor(fileName: String) {
    val tokens = fileName.split("/")
    val path = tokens.dropLast(1).joinToString("/")
    createFolder(path)
}