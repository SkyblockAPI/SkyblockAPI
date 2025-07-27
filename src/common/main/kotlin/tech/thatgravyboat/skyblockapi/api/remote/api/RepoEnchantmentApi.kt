package tech.thatgravyboat.skyblockapi.api.remote.api

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

object RepoEnchantmentApi {
    private val cache: MutableMap<String, ItemStack?> = mutableMapOf()

    fun getEnchantmentAsItemOrNull(id: String, level: Int) = cache.getOrPut(id) {
        val enchantment = RepoAPI.enchantments().getEnchantment(id)
        if (enchantment == null) return@getOrPut null

        val level = enchantment.levels().entries.sortedBy { (key) -> key }.firstOrElseLast { (key) -> key == level }.value
        if (level == null) return@getOrPut ItemStack(Items.BARRIER) {
            this[DataComponents.ITEM_NAME] = Text.of("Unknown Enchantment Level: $id")
        }
        val lore = level.lore().map { it.asComponent() }

        ItemBuilder(Items.ENCHANTED_BOOK) {
            this[DataComponents.ITEM_NAME] = Text.of("${enchantment.name()} ${level.literalLevel()}")
            this[DataComponents.LORE] = ItemLore(lore, lore)
            this[DataComponents.CUSTOM_DATA] = compoundTag {
                putString("id", "ENCHANTED_BOOK")
                putCompound("enchantments") {
                    putInt(enchantment.id(), level.level())
                }
            }.toData()
        }
    }

}
