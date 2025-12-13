package tech.thatgravyboat.skyblockapi.utils.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

inline fun JsonObject(builder: JsonObjectBuilder.() -> Unit): JsonObject {
    val json = JsonObjectBuilder()
    builder(json)
    return json.build()
}

inline fun JsonArray(builder: JsonArrayBuilder.() -> Unit): JsonArray {
    val json = JsonArrayBuilder()
    builder(json)
    return json.build()
}

class JsonObjectBuilder {

    private val json = JsonObject()

    operator fun set(key: String, value: String) = json.addProperty(key, value)
    operator fun set(key: String, value: Number) = json.addProperty(key, value)
    operator fun set(key: String, value: Boolean) = json.addProperty(key, value)
    operator fun set(key: String, value: JsonElement) = json.add(key, value)

    fun obj(key: String, builder: (JsonObjectBuilder) -> Unit) {
        val child = JsonObjectBuilder()
        builder(child)
        json.add(key, child.build())
    }

    fun arr(key: String, builder: (JsonArrayBuilder) -> Unit) {
        val child = JsonArrayBuilder()
        builder(child)
        json.add(key, child.build())
    }

    fun build(): JsonObject {
        return json
    }
}

class JsonArrayBuilder {

    private val json = JsonArray()

    fun add(value: String) = json.add(value)
    fun add(value: Number) = json.add(value)
    fun add(value: Boolean) = json.add(value)
    fun add(value: JsonElement) = json.add(value)

    fun obj(builder: (JsonObjectBuilder) -> Unit) {
        val child = JsonObjectBuilder()
        builder(child)
        json.add(child.build())
    }

    fun arr(builder: (JsonArrayBuilder) -> Unit) {
        val child = JsonArrayBuilder()
        builder(child)
        json.add(child.build())
    }

    fun build(): JsonArray {
        return json
    }
}
