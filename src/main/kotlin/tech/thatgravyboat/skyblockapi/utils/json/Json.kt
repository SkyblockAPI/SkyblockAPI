package tech.thatgravyboat.skyblockapi.utils.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.RegistryOps
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.io.InputStream
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

object Json {

    val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    private val vanillaRegistry by lazy {
        //~ if >= 26.3 'createLookup' -> 'createWorldLookup'
        VanillaRegistries.createWorldLookup()
    }

    internal val registry get() = McClient.connection?.registryAccess() ?: vanillaRegistry

    internal val ops: DynamicOps<JsonElement> get() {
        return RegistryOps.create(JsonOps.INSTANCE, LenientHolderLookupAdapter(registry))
    }
    internal val nbtOps: DynamicOps<Tag>
        get() {
            return RegistryOps.create(NbtOps.INSTANCE, LenientHolderLookupAdapter(registry))
        }

    inline fun <reified T : Any> InputStream.readJson(): T =
        gson.fromJson(bufferedReader(), typeOf<T>().javaType)

    inline fun <reified T : Any> String.readJson(): T =
        gson.fromJson(this, typeOf<T>().javaType)

    val JsonElement.isString get() = isJsonPrimitive && asJsonPrimitive.isString

    fun <T : Any> T.toJson(codec: Codec<T>): JsonElement? {
        return codec.encodeStart(ops, this).result().getOrNull()
    }

    fun <T : Any> T.toNbt(codec: Codec<T>): Tag? {
        return codec.encodeStart(nbtOps, this).result().getOrNull()
    }

    fun <T : Any> T.toJsonOrThrow(codec: Codec<T>): JsonElement {
        return codec.encodeStart(ops, this).getOrThrow()
    }

    fun <T : Any> JsonElement?.toData(codec: Codec<T>): T? {
        return codec.parse(ops, this).result().getOrNull()
    }

    fun <T : Any> JsonElement?.toDataOrThrow(codec: Codec<T>): T {
        return codec.parse(ops, this).getOrThrow()
    }

    fun JsonElement?.toPrettyString(): String = gson.toJson(this)
    fun JsonElement?.toComponent(spaces: Int = 4, newLines: Boolean = true): Component = JsonWriter.write(this, 0, spaces, newLines)
}
