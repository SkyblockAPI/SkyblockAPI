#!/usr/bin/env kotlin

import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.notExists
import kotlin.io.path.readText

fun Path.executeCommand(vararg args: String) {
    ProcessBuilder(*args)
        .redirectOutput(this.toFile())
        .directory(this.parent.toFile())
        .start()
        .waitFor()
}


enum class MemberType {
    FUN,
    FIELD,
    ;

    companion object {
        val types = entries.associateBy { it.name.lowercase() }
        fun fromName(name: String): MemberType = requireNotNull(types[name]) { "Unknown member type $name" }
    }

    override fun toString(): String {
        return name.lowercase()
    }
}

enum class Modifier(val breakingIfRemoved: Boolean, val breakingIfAdded: Boolean ) {
    // visibility
    PUBLIC(true, false),
    PROTECTED(false, false),

    // classes
    INTERFACE(true, true),
    ANNOTATION(true, true),

    // members
    STATIC(true, true),
    SYNTHETIC(false, false),

    // misc
    ABSTRACT(true, true),
    FINAL(false, true),
    ;

    companion object {
        val modifiers = entries.associateBy { it.name.lowercase() }
        fun fromName(name: String): Modifier = requireNotNull(modifiers[name]) { "Unknown modifier '$name'" }
    }

    override fun toString(): String {
        return name.lowercase()
    }
}

data class Member(
    val type: MemberType, // fun or field
    val name: String,
    val desc: String?,
    val returnType: String?,
    val modifiers: Set<Modifier>,
)

data class ApiClass(
    val name: String, // fqname
    val modifiers: Set<Modifier>,
    val superTypes: Set<String>, // fqnames of the supertypes
    val members: MutableList<Member> = mutableListOf(), // functions and fields
) {
    fun prettyPrint(includeSuper: Boolean = false): String = buildString {
        modifiers.forEach {
            append(it.toString())
            append(" ")
        }
        append(name)
        if (superTypes.isNotEmpty()) {
            append(" : ")
            append(superTypes.joinToString(", "))
        }
    }
}

private fun MatchResult.groupOrNull(key: String): String? = groups[key]?.value
private fun MatchResult.group(key: String): String = groups[key]!!.value

data class StringReader(val text: String, var index: Int = 0) {
    fun skipWhitespace() {
        while (canRead() && text[index].isWhitespace()) {
            index++
        }
    }

    fun ident(): String {
        val next = text.indexOf(' ', index).takeUnless { it == -1 }!!
        return text.substring(index, next).also {
            index = next
            skipWhitespace()
        }
    }

    fun fqn(vararg termination: Char): String? {
        val next = termination.minOf { text.indexOf(it, index).let { if (it < 0) Int.MAX_VALUE else it } }.takeUnless { it == -1 }!!
        val sub = text.substring(index, next).trim()
        if (sub.contains("\n")) {
            return null
        }
        index = next
        skipWhitespace()
        return sub
    }

    fun expect(char: Char) {
        val actual = text[index]
        if (actual == char) {
            index++
            skipWhitespace()
            return
        }
        throw RuntimeException()
    }

    fun expect(literal: String) {
        if (text.substring(index, literal.length + index) == literal) {
            index += literal.length
            skipWhitespace()
            return
        }
        throw RuntimeException()
    }

    fun canRead(): Boolean {
        return index < text.length
    }

    fun match(char: Char): Boolean {
        if (text[index] == char) {
            index++
            skipWhitespace()
            return true
        }

        return false
    }

    fun next(): String {
        return text[index++].toString().also { skipWhitespace() }
    }

    fun type(): String = when(val t = next()) {
        "[" -> "[" + type()
        "L" -> "L" + fqn(';').also { expect(';') }
        else -> t
    }

}

fun parseApi(text: String): Map<String, ApiClass> {
    // name -> ApiClass
    val classes = mutableMapOf<String, ApiClass>()
    val reader = StringReader(text)
    while (reader.canRead()) {
        val modifiers: MutableSet<Modifier> = mutableSetOf()
        var current = reader.ident()
        while (current != "class") {
            //print("$current ")
            modifiers.add(Modifier.fromName(current))
            current = reader.ident()
        }
        //print("$current ")
        val fqn = reader.fqn(':', '{') ?: throw RuntimeException("Expected fqn to be present")
        //print("$fqn ")
        val supers = mutableSetOf<String>()
        if (reader.match(':')) {
            //print(": ")
            while (true) {
                val superclass = reader.fqn(',', '{')!!
                //print("$superclass ")
                supers.add(superclass)
                if (reader.match(',')) {
                    continue
                }
                break
            }
        }
        //println("{ ")

        reader.expect('{')

        val currentClass = ApiClass(fqn, modifiers, supers)
        classes[fqn] = currentClass

        while (!reader.match('}')) {
            var type = reader.ident()
            val memberModifiers = mutableSetOf<Modifier>()
            //print("  ")
            while (type != "field" && type != "fun") {
                //print("mod: $type, ")
                memberModifiers.add(Modifier.fromName(type))
                type = reader.ident()
            }
            //print("type: $type, ")

            if (type == "field") {
                val line = reader.fqn('\n')!!.trim()
                val name: String
                val returnType: String
                if (line.endsWith(";")) { // this means the field has a non-primitive type
                    val reversed = line.reversed()
                    val firstSlash = line.indexOf("/")
                    val typeStart = line.length - reversed.indexOf("L ", line.length - firstSlash)
                    name = line.substring(0, typeStart)
                    returnType = line.substring(typeStart)
                } else {
                    name = line.substringBeforeLast(' ')
                    returnType = line.substringAfterLast(' ')
                }
                //print("name: $name, ")
                //println("ret: $returnType, ")
                currentClass.members.add(Member(
                    MemberType.fromName(type),
                    name,
                    null,
                    returnType,
                    memberModifiers
                ))
            } else {
                val name = reader.fqn('(')!!
                //print("name: $name, ")
                reader.expect('(')
                val signature = reader.fqn(')')
                //print("sign: $signature, ")
                reader.expect(')')
                val returnType = reader.type()
                //println("ret: $returnType, ")
                currentClass.members.add(Member(
                    MemberType.fromName(type),
                    name,
                    signature,
                    returnType,
                    memberModifiers
                ))
            }
        }

        //println("}")
    }

    require(!reader.canRead()) { "Didn't parse correctly" }
    return classes
}

val versioned = mutableMapOf<String, MutableSet<String>>()

__FILE__.toPath().toAbsolutePath().parent.parent.parent.resolve("versions").listDirectoryEntries().forEach {
    val baseApiPath = it.resolve("api/${it.fileName}.api.old")
    val apiPath = it.resolve("api/${it.fileName}.api")

    if (baseApiPath.notExists() || apiPath.notExists()) {
        return@forEach
    }

    val baseApi = parseApi(baseApiPath.readText())
    val api = parseApi(apiPath.readText())

    val errors = mutableSetOf<StringBuilder>()

    baseApi.forEach { (fqn, apiClass) ->
        val builder by lazy {
            StringBuilder().apply {
                errors.add(this)
                appendLine(apiClass.prettyPrint())
            }
        }
        fun emitError(message: String) {
            builder.append("  ").appendLine(message)
        }
        val newApiClass = api[fqn] ?: return@forEach emitError("Was fully removed!")

        val removedModifiers = (apiClass.modifiers - newApiClass.modifiers).filter { it.breakingIfRemoved }
        val addedModifiers = (newApiClass.modifiers - apiClass.modifiers).filter { it.breakingIfAdded }

        if (removedModifiers.isNotEmpty()) {
            emitError("Invalid modifier modifications, removed: ${removedModifiers.joinToString(", ")}")
        }
        if (addedModifiers.isNotEmpty()) {
            emitError("Invalid modifier modifications, added: ${addedModifiers.joinToString(", ")}")
        }

        apiClass.members.forEach {
            val lazy by lazy {
                emitError("${it.type} ${it.name}(${it.desc})${it.returnType}")
            }
            fun emitMemberError(message: String) {
                lazy // prepends member
                emitError("  $message")
            }
            val newMember = newApiClass.members.find { newMember ->
                newMember.name == it.name && newMember.desc == it.desc && newMember.returnType == it.returnType
            } ?: return@forEach emitMemberError("Was fully removed!")

            val removedMemberModifier = (it.modifiers - newMember.modifiers).filter { it.breakingIfRemoved }
            val addedMemberModifier = (newMember.modifiers - it.modifiers).filter { it.breakingIfAdded }

            if (removedMemberModifier.isNotEmpty()) {
                emitMemberError("Invalid modifier modifications, removed: ${removedMemberModifier.joinToString(", ")}")
            }
            if (addedMemberModifier.isNotEmpty()) {
                emitMemberError("Invalid modifier modifications, added: ${addedMemberModifier.joinToString(", ")}")
            }
        }

    }

    if (errors.isNotEmpty()) {
        versioned[it.fileName.toString()] = errors.mapTo(mutableSetOf()) { it.toString() }
    }
}

val inAll = mutableSetOf<String>()

val keys = versioned.keys

versioned.values.flatten().distinct().forEach {
    if (keys.all { versioned[it]?.contains(it) == true }) {
        inAll.add(it)
    }
}

buildString {
    if (inAll.isNotEmpty()) {
        appendLine("All versions")
        inAll.forEach(::appendLine)
        appendLine()
    }

    versioned.forEach { (version, errors) ->
        val errors = errors.filterNot { inAll.contains(it) }
        if (errors.isEmpty()) return@forEach
        appendLine("Version $version")
        appendLine()
        errors.forEach(::appendLine)
        appendLine()
    }
}.trimEnd().let(::println)

