package tech.thatgravyboat.skyblockapi.api.remote.api

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.TooltipDisplay
import tech.thatgravyboat.repolib.api.PotionsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

object RepoPotionsAPI {
    private val cache: MutableMap<String, ItemStack?> = mutableMapOf()

    fun getPotionById(id: String): PotionsAPI.Potion? {
        if (!RepoAPI.isInitialized()) return null
        if (id.lowercase() == "water") {
            return RepoAPI.potions().potions().values.find { it.type == null }
        }
        return RepoAPI.potions().getPotion(id)
    }

    fun getPotionAsItemOrNull(id: String, level: Int? = null): ItemStack? {
        if (!RepoAPI.isInitialized()) return null
        return cache.getOrPut("$id:$level") {
            val potion = getPotionById(id) ?: return@getOrPut null
            val level = potion.levels().values
                .sortedBy(PotionsAPI.PotionLevel::level)
                .firstOrElseLast { it.level() == level }

            if (level == null) return@getOrPut null
            val lore = level.lore().map { it.asComponent() }


            val item = if (level.splash) Items.SPLASH_POTION else Items.POTION
            ItemBuilder(item) {
                val isActualPotion = potion.type == "POTION"
                val levelSuffix = if (isActualPotion) " ${level.literalLevel()}" else ""
                this[DataComponents.ITEM_NAME] = Text.of("${potion.name()}$levelSuffix")
                this[DataComponents.CUSTOM_NAME] = Text.of("${potion.name()}$levelSuffix") {
                    this.italic = false
                }
                this[DataComponents.LORE] = ItemLore(lore, lore)
                this[DataComponents.CUSTOM_DATA] = compoundTag {
                    putString("id", "POTION")
                    putBoolean("enhanced", false)
                    putBoolean("extended", false)
                    putNullableString("potion", potion.internalPotion())
                    putNullableString("potion_type", potion.type)
                    putInt("potion_level", level.level)
                }.toData()
                this[DataComponents.TOOLTIP_DISPLAY] = TooltipDisplay.DEFAULT.withHidden(DataComponents.POTION_CONTENTS, true)
                BuiltInRegistries.POTION.get(Identifiers.of(potion.vanillaEffect())).map(::PotionContents).ifPresent {
                    this[DataComponents.POTION_CONTENTS] = it
                }
            }
        }
    }

    fun getPotionAsItem(id: String, level: Int? = null): ItemStack {
        return getPotionAsItemOrNull(id, level) ?: ItemStack(Items.BARRIER) {
            this[DataComponents.ITEM_NAME] = Text.of("Unknown Potion: $id:${level ?: "?"}")
        }
    }

    fun createId(type: String?, internalPotion: String?, level: Int?): SkyBlockId = when {
        type == null -> "water"
        type != "POTION" -> type
        internalPotion == null -> SkyBlockId.UNKNOWN
        level != null -> "$internalPotion:$level"
        else -> internalPotion
    }.let(SkyBlockId::potion)

}
