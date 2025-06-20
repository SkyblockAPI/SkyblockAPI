package tech.thatgravyboat.skyblockapi.utils.extensions

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

actual fun Entity.save(): CompoundTag {
    val tag = CompoundTag()
    tag.putString("id", EntityType.getKey(this.type).toString())
    return this.saveWithoutId(tag)
}
