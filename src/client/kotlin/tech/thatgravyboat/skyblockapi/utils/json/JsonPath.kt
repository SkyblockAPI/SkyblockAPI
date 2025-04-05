package tech.thatgravyboat.skyblockapi.utils.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.brigadier.StringReader
import org.intellij.lang.annotations.Language


fun JsonElement.getPath(@Language("JSONPath") path: String, createIfMissing: Boolean = false): JsonElement? {
    val reader = StringReader(path)
    var current: JsonElement? = this

    fun JsonElement?.getOrCreate(name: String): JsonElement? {
        (this as? JsonObject)?.let {
            if (createIfMissing && !it.has(name)) {
                val newObject = JsonObject()
                it.add(name, newObject)
                return newObject
            }
            return it.get(name)
        }
        return null
    }

    while (reader.canRead()) {
        current ?: return null

        when (reader.peek()) {
            '.' -> reader.skip()
            '[' -> {
                reader.skip()
                val key = reader.readStringUntil(']')
                reader.skip()

                current = if ((key.startsWith("\"") && key.endsWith("\"")) || (key.startsWith("'") && key.endsWith("'"))) {
                    (current as? JsonObject).getOrCreate(key.substring(1, key.length - 1))
                } else if (current is JsonArray) {
                    var index = key.toIntOrNull() ?: return null
                    index = if (index < 0) current.size() + index else index

                    if (index in 0 until current.size()) {
                        current[index]
                    } else {
                        null
                    }
                } else {
                    null
                }
            }

            '"', '\'' -> {
                val key = reader.readString()
                current = (current as? JsonObject).getOrCreate(key)
            }

            else -> {
                val start = reader.cursor
                while (reader.canRead() && isAllowedInUnquotedName(reader.peek())) {
                    reader.skip()
                }

                val key = reader.string.substring(start, reader.cursor)
                current = (current as? JsonObject).getOrCreate(key)
            }
        }
    }

    return current
}

private fun isAllowedInUnquotedName(char: Char): Boolean {
    return char != ' ' && char != '"' && char != '\'' && char != '[' && char != ']' && char != '.' && char != '{' && char != '}'
}
