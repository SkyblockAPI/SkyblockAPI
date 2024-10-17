package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

val ItemStack.tag: CompoundTag? get() = this.get(DataComponents.CUSTOM_DATA)?.unsafe
fun ItemStack.getTag(key: String): Tag? = this.tag?.get(key)

fun ItemStack.getRawLore(): List<String> {
    val lore = this.get(DataComponents.LORE) ?: return emptyList()
    return lore.lines.map { it.stripped }
}

val ItemStack.cleanName: String get() = hoverName.stripped
