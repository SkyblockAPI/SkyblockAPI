package tech.thatgravyboat.skyblockapi.utils.extensions

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity

actual fun Entity.saveWithoutId(tag: CompoundTag): CompoundTag {
    return this.saveWithoutId(tag)
}
