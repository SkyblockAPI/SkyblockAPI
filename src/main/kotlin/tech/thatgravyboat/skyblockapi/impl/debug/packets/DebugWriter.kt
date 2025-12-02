package tech.thatgravyboat.skyblockapi.impl.debug.packets

import com.google.gson.*
import com.mojang.serialization.Codec
import com.mojang.util.InstantTypeAdapter
import com.mojang.util.UUIDTypeAdapter
import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.SnbtPrinterTagVisitor
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.numbers.NumberFormat
import net.minecraft.network.chat.numbers.NumberFormatTypes
import net.minecraft.network.protocol.Packet
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.spongepowered.asm.mixin.Unique
import tech.thatgravyboat.skyblockapi.impl.debug.packets.CodecJsonSerializer.Companion.registerTypeAdapter
import tech.thatgravyboat.skyblockapi.impl.debug.packets.SimpleJsonSerializer.Companion.registerTypeAdapter
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.util.Optional
import java.util.UUID

object DebugWriter {

    private val gson = GsonBuilder()
        .disableHtmlEscaping()
        .enableComplexMapKeySerialization()
        .serializeNulls()
        .setExclusionStrategies(SkipMixinUniqueFields)
        .registerTypeAdapter(UUID::class.java, UUIDTypeAdapter())
        .registerTypeAdapter(java.time.Instant::class.java, InstantTypeAdapter())
        .registerTypeAdapter<EntityType<*>>(BuiltInRegistries.ENTITY_TYPE.byNameCodec())
        .registerTypeAdapter<Block>(BuiltInRegistries.BLOCK.byNameCodec())
        .registerTypeAdapter<Item>(BuiltInRegistries.ITEM.byNameCodec())
        .registerTypeAdapter<Attribute>(BuiltInRegistries.ATTRIBUTE.byNameCodec())
        .registerTypeAdapter<Component>(ComponentSerialization.CODEC)
        .registerTypeAdapter<ResourceLocation>(ResourceLocation.CODEC)
        .registerTypeAdapter<ItemStack>(ItemStack.OPTIONAL_CODEC)
        .registerTypeAdapter<BlockState>(BlockState.CODEC)
        .registerTypeAdapter<ParticleOptions>(ParticleTypes.CODEC)
        .registerTypeAdapter<NumberFormat>(NumberFormatTypes.CODEC)
        .registerTypeAdapter<Tag> { tag, _ -> JsonPrimitive(SnbtPrinterTagVisitor().visit(tag)) }
        .registerTypeAdapter<Optional<*>> { optional, context -> optional.map(context::serialize).orElse(JsonNull.INSTANCE) }
        .registerTypeAdapter<EntityDataSerializer<*>> { serializer, _ -> serializer.toName()?.let(::JsonPrimitive) ?: JsonNull.INSTANCE }
        .registerTypeAdapter<Holder<*>> { holder, context -> holder.unwrap().map({ context.serialize(it.location()) }, { context.serialize(it) }) }
        .create()

    fun Packet<*>.toJson(): JsonElement = gson.toJsonTree(this)
}

private object SkipMixinUniqueFields : ExclusionStrategy {
    override fun shouldSkipField(attributes: FieldAttributes): Boolean {
        return attributes.getAnnotation(Unique::class.java) != null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean = false
}

private fun EntityDataSerializer<*>.toName(): String? {
    for (field in EntityDataSerializers::class.java.declaredFields) {
        if (Modifier.isPublic(field.modifiers) && Modifier.isStatic(field.modifiers) && field.type == EntityDataSerializer::class.java) {
            val value = field.get(null)
            if (value == this) {
                return field.name
            }
        }
    }
    return null
}

private class SimpleJsonSerializer<T>(val serializer: (T, JsonSerializationContext) -> JsonElement?) : JsonSerializer<T> {
    override fun serialize(data: T, type: Type, context: JsonSerializationContext): JsonElement? {
        return serializer.invoke(data, context)
    }

    companion object {

        inline fun <reified T> GsonBuilder.registerTypeAdapter(noinline serializer: (T, JsonSerializationContext) -> JsonElement?): GsonBuilder {
            return this.registerTypeAdapter(T::class.java, SimpleJsonSerializer(serializer))
        }
    }
}

private class CodecJsonSerializer<T>(val codec: Codec<T & Any>) : JsonSerializer<T> {

    override fun serialize(data: T, type: Type, context: JsonSerializationContext): JsonElement {
        return data?.toJsonOrThrow(codec) ?: JsonNull.INSTANCE
    }

    companion object {

        inline fun <reified T> GsonBuilder.registerTypeAdapter(codec: Codec<T & Any>): GsonBuilder {
            return this.registerTypeAdapter(T::class.java, CodecJsonSerializer(codec))
        }
    }
}
