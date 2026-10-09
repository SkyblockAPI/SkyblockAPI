package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.component.CustomData
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public fun <T : Any> getCompoundTagFunctionByType(type: KType): (CompoundTag, String) -> T? {
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

public fun CompoundTag.getStringOrNull(key: String): String? = this.getString(key).getOrNull()
public fun CompoundTag.getByteOrNull(key: String): Byte? = this.getByte(key).getOrNull()
public fun CompoundTag.getShortOrNull(key: String): Short? = this.getShort(key).getOrNull()
public fun CompoundTag.getIntOrNull(key: String): Int? = this.getInt(key).getOrNull()
public fun CompoundTag.getLongOrNull(key: String): Long? = this.getLong(key).getOrNull()
public fun CompoundTag.getFloatOrNull(key: String): Float? = this.getFloat(key).getOrNull()
public fun CompoundTag.getDoubleOrNull(key: String): Double? = this.getDouble(key).getOrNull()
public fun CompoundTag.getBooleanOrNull(key: String): Boolean? = this.getBoolean(key).getOrNull()
public fun CompoundTag.getObjectOrNull(key: String): CompoundTag? = this.getCompound(key).getOrNull()

public fun CompoundTag.getUuidOrNull(key: String): UUID? = this.getStringOrNull(key)?.runCatching(UUID::fromString)?.getOrNull()

public fun compoundTag(init: CompoundTag.() -> Unit) = CompoundTag().apply(init)
public fun CompoundTag.putCompound(key: String, init: CompoundTag.() -> Unit) = this.put(key, compoundTag(init))
public fun CompoundTag.toData(): CustomData = CustomData.of(this)


public fun CompoundTag.putNullableString(key: String, value: String?): Unit = value?.let { this.putString(key, it) } ?: run {
    this.remove(key)
}
