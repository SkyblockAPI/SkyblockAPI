package tech.thatgravyboat.skyblockapi.utils.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

inline fun JsonObject(builder: JsonObjectBuilder.() -> Unit): JsonObject = JsonObjectBuilder.invoke(builder)

inline fun JsonArray(builder: JsonArrayBuilder.() -> Unit): JsonArray = JsonArrayBuilder.invoke(builder)

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

    companion object {
        inline operator fun invoke(builder: JsonObjectBuilder.() -> Unit): JsonObject {
            return JsonObjectBuilder().apply(builder).build()
        }
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

    companion object {
        inline operator fun invoke(builder: JsonArrayBuilder.() -> Unit): JsonArray {
            return JsonArrayBuilder().apply(builder).build()
        }
    }
}
