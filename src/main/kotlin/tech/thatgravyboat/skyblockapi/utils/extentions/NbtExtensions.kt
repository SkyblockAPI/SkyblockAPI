package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.component.CustomData
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KType
import kotlin.reflect.typeOf

fun <T : Any> getCompoundTagFunctionByType(type: KType): (CompoundTag, String) -> T? {
    @Suppress("UNCHECKED_CAST")
    return when(type) {
        typeOf<String>() -> CompoundTag::getStringOrNull
        typeOf<Byte>() -> CompoundTag::getByteOrNull
        typeOf<Short>() -> CompoundTag::getShortOrNull
        typeOf<Int>() -> CompoundTag::getIntOrNull
        typeOf<Long>() -> CompoundTag::getLongOrNull
        typeOf<Float>() -> CompoundTag::getFloatOrNull
        typeOf<Double>() -> CompoundTag::getDoubleOrNull
        typeOf<Boolean>() -> CompoundTag::getBooleanOrNull
        typeOf<UUID>() -> CompoundTag::getUuidOrNull
        else -> throw IllegalArgumentException("$type is not supported!")
    } as (CompoundTag, String) -> T?
}

fun CompoundTag.getStringOrNull(key: String): String? = this.getString(key).getOrNull()
fun CompoundTag.getByteOrNull(key: String): Byte? = this.getByte(key).getOrNull()
fun CompoundTag.getShortOrNull(key: String): Short? = this.getShort(key).getOrNull()
fun CompoundTag.getIntOrNull(key: String): Int? = this.getInt(key).getOrNull()
fun CompoundTag.getLongOrNull(key: String): Long? = this.getLong(key).getOrNull()
fun CompoundTag.getFloatOrNull(key: String): Float? = this.getFloat(key).getOrNull()
fun CompoundTag.getDoubleOrNull(key: String): Double? = this.getDouble(key).getOrNull()
fun CompoundTag.getBooleanOrNull(key: String): Boolean? = this.getBoolean(key).getOrNull()
fun CompoundTag.getObjectOrNull(key: String): CompoundTag? = this.getCompound(key).getOrNull()

fun CompoundTag.getUuidOrNull(key: String): UUID? = this.getStringOrNull(key)?.runCatching(UUID::fromString)?.getOrNull()

fun compoundTag(init: CompoundTag.() -> Unit) = CompoundTag().apply(init)
fun CompoundTag.putCompound(key: String, init: CompoundTag.() -> Unit) = this.put(key, compoundTag(init))
fun CompoundTag.toData(): CustomData = CustomData.of(this)


fun CompoundTag.putNullableString(key: String, value: String?) = value?.let { this.putString(key, it) }
