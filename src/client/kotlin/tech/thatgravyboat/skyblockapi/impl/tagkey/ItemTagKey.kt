package tech.thatgravyboat.skyblockapi.impl.tagkey

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

enum class ItemTagKey(path: String) {
    GLASS_PANES("glass_panes"),
    ;

    val key = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("skyblockapi", path),
    )
}
