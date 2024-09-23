package tech.thatgravyboat.skyblockapi.utils.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.InputStream
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

object Json {

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    inline fun <reified T : Any> InputStream.readJson(): JsonObject =
        gson.fromJson(bufferedReader(), typeOf<T>().javaType)

    val JsonElement.isString get() = isJsonPrimitive && asJsonPrimitive.isString
}
