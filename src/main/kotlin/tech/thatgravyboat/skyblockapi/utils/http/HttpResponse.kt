package tech.thatgravyboat.skyblockapi.utils.http

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import java.io.InputStream
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

data class HttpResponse(
    val statusCode: Int,
    val headers: Map<String, List<String>>,
    private val stream: InputStream
) {

    val status: String
        get() = when (statusCode) {
            200 -> "OK"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Not Found"
            500 -> "Internal Server Error"
            else -> "Unknown"
        }

    val isOk: Boolean
        get() = statusCode == 200

    fun asText(): String = stream.bufferedReader().use { it.readText() }

    inline fun <reified T : Any> asJson(gson: Gson): T = gson.fromJson(asText(), typeOf<T>().javaType)
    inline fun <reified T : Any> asCodec(codec: Codec<T>): T = asText().readJson<JsonElement>().toDataOrThrow(codec)
}
