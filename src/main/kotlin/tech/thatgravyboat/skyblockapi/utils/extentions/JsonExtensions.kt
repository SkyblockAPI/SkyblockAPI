package tech.thatgravyboat.skyblockapi.utils.extentions

import com.google.gson.JsonElement
import java.util.UUID

private fun <T> JsonElement?.parse(default: T, mapper: (JsonElement) -> T): T = this?.runCatching {
    mapper(this)
}?.getOrNull() ?: default

public fun JsonElement?.asBoolean(default: Boolean): Boolean = parse(default) { it.asBoolean }
public fun JsonElement?.asInt(default: Int): Int = parse(default) { it.asInt }
public fun JsonElement?.asLong(default: Long): Long = parse(default) { it.asLong }
public fun JsonElement?.asDouble(default: Double): Double = parse(default) { it.asDouble }
public fun JsonElement?.asShort(default: Short): Short = parse(default) { it.asShort }

public fun JsonElement?.asUUID(): UUID? = parse(null) { UUID.fromString(it.asString) }
public fun JsonElement?.asUUID(default: UUID): UUID = parse(default) { UUID.fromString(it.asString) }

public fun JsonElement?.asString(): String? = parse(null) { it.asString }
public fun JsonElement?.asString(default: String): String = parse(default) { it.asString }

public fun <K, V> JsonElement?.asMap(mapper: (String, JsonElement) -> Pair<K, V>): Map<K, V> =
    parse(emptyMap()) { it.asJsonObject.entrySet().associate { mapper(it.key, it.value) } }

public fun <T> JsonElement?.asList(mapper: (JsonElement) -> T): List<T> = parse(emptyList()) { it.asJsonArray.map(mapper) }
public fun JsonElement?.asStringList(): List<String> = asList { it.asString }

public inline fun <reified T : Enum<T>> JsonElement?.asEnum(mapper: (T) -> String = { it.name }): T? {
    val content = this.asString() ?: return null
    return T::class.java.enumConstants.firstOrNull { mapper(it).equals(content, true) }
}
