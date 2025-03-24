package tech.thatgravyboat.skyblockapi.utils.extentions

import com.google.gson.JsonElement
import java.util.*

private fun <T> JsonElement?.parse(default: T, mapper: (JsonElement) -> T): T = this?.runCatching {
    mapper(this)
}?.getOrNull() ?: default

fun JsonElement?.asBoolean(default: Boolean): Boolean = parse(default) { it.asBoolean }
fun JsonElement?.asInt(default: Int): Int = parse(default) { it.asInt }
fun JsonElement?.asLong(default: Long): Long = parse(default) { it.asLong }
fun JsonElement?.asDouble(default: Double): Double = parse(default) { it.asDouble }
fun JsonElement?.asShort(default: Short): Short = parse(default) { it.asShort }

fun JsonElement?.asUUID(): UUID? = parse(null) { UUID.fromString(it.asString) }
fun JsonElement?.asUUID(default: UUID): UUID = parse(default) { UUID.fromString(it.asString) }
fun JsonElement?.asString(default: String): String = parse(default) { it.asString }
fun <K, V> JsonElement?.asMap(mapper: (String, JsonElement) -> Pair<K, V>): Map<K, V> =
    parse(emptyMap<K, V>()) { it.asJsonObject.entrySet().associate { mapper(it.key, it.value) } }

fun <T> JsonElement?.asList(mapper: (JsonElement) -> T): List<T> = parse(emptyList()) { it.asJsonArray.map(mapper) }
