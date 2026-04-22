package tech.thatgravyboat.skyblockapi.api.repo.apis

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.TooltipDisplay
import tech.thatgravyboat.repolib.api.PotionsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.extentions.compoundTag
import tech.thatgravyboat.skyblockapi.utils.extentions.firstOrElseLast
import tech.thatgravyboat.skyblockapi.utils.extentions.putNullableString
import tech.thatgravyboat.skyblockapi.utils.extentions.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

object SkyBlockPotionsRepo : RepoItemCacheAsQuery<SkyBlockPotionsRepo.Query>("Potions", ::Query) {

    private val repo get() = RepoAPI.potions()

    override fun create(key: Query): LazyItemStack? {
        val potion = get(key.id) ?: return null
        val level = potion.levels().values
            .sortedBy(PotionsAPI.PotionLevel::level)
            .firstOrElseLast { it.level() == key.level }

        if (level == null) return null
        val lore = level.lore().map { it.asComponent() }


        val item = if (level.splash) Items.SPLASH_POTION else Items.POTION
        return LazyItemStack(item) {
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

    fun get(id: String): PotionsAPI.Potion? = ifInitialized {
        if (id.lowercase() == "water") {
            return repo.potions().values.find { it.type == null }
        }
        return repo.getPotion(id)
    }

    data class Query(
        var id: String = "",
        var level: Int? = null,
    )

    fun createId(type: String?, internalPotion: String?, level: Int?): SkyBlockId = when {
        type == null -> "water"
        type != "POTION" -> type
        internalPotion == null -> SkyBlockId.UNKNOWN
        level != null -> "$internalPotion:$level"
        else -> internalPotion
    }.let(SkyBlockId::potion)
}
