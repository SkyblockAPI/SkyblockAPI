package tech.thatgravyboat.skyblockapi.impl.tagkey

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

enum class ItemTag(path: String) : ItemTagKey {
    GLASS_PANES("glass_panes"),
    HOTM_PERK_ITEMS("hotm_perk_items"),
    ;

    override val key: TagKey<Item> = TagKey.create(Registries.ITEM, SkyBlockAPI.id(path))
}
