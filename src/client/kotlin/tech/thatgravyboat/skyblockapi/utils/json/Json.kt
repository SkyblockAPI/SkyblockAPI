package tech.thatgravyboat.skyblockapi.utils.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import java.io.InputStream
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

object Json {

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    inline fun <reified T : Any> InputStream.readJson(): T =
        gson.fromJson(bufferedReader(), typeOf<T>().javaType)

    inline fun <reified T : Any> String.readJson(): T =
        gson.fromJson(this, typeOf<T>().javaType)

    val JsonElement.isString get() = isJsonPrimitive && asJsonPrimitive.isString

    fun <T : Any> T.toJson(codec: Codec<T>): JsonElement? {
        val ops = if (McLevel.hasLevel) McLevel.registry.createSerializationContext(JsonOps.INSTANCE) else JsonOps.INSTANCE
        return codec.encodeStart(ops, this).result().getOrNull()
    }

    fun JsonElement?.toPrettyString(): String = gson.toJson(this)
}
