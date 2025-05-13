package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.Text

data class TrophyFish(val type: TrophyFishType, val tier: TrophyFishTier) {
    val item: ItemStack by lazy { type.getItem(tier) }
    val displayName: Component by lazy {
        if (tier == TrophyFishTier.NONE) {
            return@lazy Component.empty().append(type.displayName)
        }

        Text.join(type.displayName, " ", tier.nameSuffix)
    }

    val apiName by lazy {
        if (tier == TrophyFishTier.NONE) {
            return@lazy type.internalName.lowercase()
        }

        "${type.internalName.lowercase()}_${tier.name.lowercase()}"
    }

    companion object {
        fun fromString(fish: String): TrophyFish? {
            if (fish.contains("/")) {
                return fish.split("/").let {
                    TrophyFish(
                        TrophyFishType.getByInternalName(it[0]) ?: return null,
                        TrophyFishTier.getByName(it[1]),
                    )
                }
            }
            return null
        }
    }
}
