package tech.thatgravyboat.skyblockapi.api.remote.api

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.EnchantsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

object RepoEnchantmentAPI {
    private val cache: MutableMap<String, ItemStack?> = mutableMapOf()

    fun getEnchantmentById(id: String) = RepoAPI.enchantments().getEnchantment(id)

    fun getEnchantmentAsItemOrNull(id: String, level: Int? = null) = cache.getOrPut("$id:$level") {
        val enchantment = getEnchantmentById(id) ?: return@getOrPut null
        val level = enchantment.levels().values
            .sortedBy(EnchantsAPI.EnchantLevel::level)
            .firstOrElseLast { it.level() == level }

        if (level == null) return@getOrPut null
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

    fun getEnchantmentAsItem(id: String, level: Int? = null): ItemStack {
        return getEnchantmentAsItemOrNull(id, level) ?: ItemStack(Items.BARRIER) {
            this[DataComponents.ITEM_NAME] = Text.of("Unknown Enchantment: $id:${level ?: "?"}")
        }
    }

}
