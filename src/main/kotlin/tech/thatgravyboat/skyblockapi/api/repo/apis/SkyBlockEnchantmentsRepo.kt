package tech.thatgravyboat.skyblockapi.api.repo.apis

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.EnchantsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.compoundTag
import tech.thatgravyboat.skyblockapi.utils.extentions.firstOrElseLast
import tech.thatgravyboat.skyblockapi.utils.extentions.putCompound
import tech.thatgravyboat.skyblockapi.utils.extentions.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

object SkyBlockEnchantmentsRepo : RepoItemCacheAsQuery<SkyBlockEnchantmentsRepo.Query>("Enchantments", ::Query) {

    private val repo get() = RepoAPI.enchantments()

    override fun create(key: Query): LazyItemStack? {
        val enchantment = get(key.id) ?: return null
        val enchantmentLevel = enchantment.levels().values.sortedBy(EnchantsAPI.EnchantLevel::level).firstOrElseLast { it.level() == key.level } ?: return null
        val lore = enchantmentLevel.lore().map { it.asComponent() }

        return LazyItemStack(Items.ENCHANTED_BOOK) {
            this[DataComponents.ITEM_NAME] = Text.of("${enchantment.name()} ${enchantmentLevel.literalLevel()}")
            this[DataComponents.LORE] = ItemLore(lore, lore)
            this[DataComponents.CUSTOM_DATA] = compoundTag {
                putString("id", "ENCHANTED_BOOK")
                putCompound("enchantments") {
                    putInt(enchantment.id(), enchantmentLevel.level())
                }
            }.toData()
        }
    }

    fun get(id: String): EnchantsAPI.Enchant? = ifInitialized { this.repo.getEnchantment(id) }

    data class Query(
        var id: String = "",
        var level: Int? = null
    )
}
