package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.nbt.CompoundTag
import java.util.*
import kotlin.jvm.optionals.getOrNull

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
