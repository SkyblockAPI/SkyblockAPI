package tech.thatgravyboat.skyblockapi.impl.tagkey

import net.fabricmc.fabric.api.tag.client.v1.ClientTags
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

enum class ItemTagKey(path: String) {
    GLASS_PANES("glass_panes"),
    ;

    val key = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("skyblockapi", path),
    )

    operator fun contains(stack: ItemStack): Boolean = stack.item in this
    operator fun contains(item: Item): Boolean = ClientTags.isInWithLocalFallback(key, item)
}
