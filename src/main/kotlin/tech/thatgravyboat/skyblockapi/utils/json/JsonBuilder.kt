package tech.thatgravyboat.skyblockapi.utils.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

public inline fun JsonObject(builder: JsonObjectBuilder.() -> Unit): JsonObject = JsonObjectBuilder.invoke(builder)

public inline fun JsonArray(builder: JsonArrayBuilder.() -> Unit): JsonArray = JsonArrayBuilder.invoke(builder)

public class JsonObjectBuilder {

    private val json = JsonObject()

    public operator fun set(key: String, value: String): Unit = json.addProperty(key, value)
    public operator fun set(key: String, value: Number): Unit = json.addProperty(key, value)
    public operator fun set(key: String, value: Boolean): Unit = json.addProperty(key, value)
    public operator fun set(key: String, value: JsonElement): Unit = json.add(key, value)

    public fun obj(key: String, builder: (JsonObjectBuilder) -> Unit) {
        val child = JsonObjectBuilder()
        builder(child)
        json.add(key, child.build())
    }

    public fun arr(key: String, builder: (JsonArrayBuilder) -> Unit) {
        val child = JsonArrayBuilder()
        builder(child)
        json.add(key, child.build())
    }

    public fun build(): JsonObject {
        return json
    }

    companion object {
        inline operator fun invoke(builder: JsonObjectBuilder.() -> Unit): JsonObject {
            return JsonObjectBuilder().apply(builder).build()
        }
    }
}

public class JsonArrayBuilder {

    private val json = JsonArray()

    public fun add(value: String): Unit = json.add(value)
    public fun add(value: Number): Unit = json.add(value)
    public fun add(value: Boolean): Unit = json.add(value)
    public fun add(value: JsonElement): Unit = json.add(value)

    public fun obj(builder: (JsonObjectBuilder) -> Unit) {
        val child = JsonObjectBuilder()
        builder(child)
        json.add(child.build())
    }

    public fun arr(builder: (JsonArrayBuilder) -> Unit) {
        val child = JsonArrayBuilder()
        builder(child)
        json.add(child.build())
    }

    public fun build(): JsonArray {
        return json
    }

    companion object {
        inline operator fun invoke(builder: JsonArrayBuilder.() -> Unit): JsonArray {
            return JsonArrayBuilder().apply(builder).build()
        }
    }
}
