package tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.utils.text.Text

data class TrophyFrog(val type: TrophyFrogType, val tier: TrophyTier) {
    val item: ItemStack by lazy { type.getItem(tier) }
    val displayName: Component by lazy {
        if (tier == TrophyTier.NONE) {
            return@lazy type.displayName.copy()
        }

        Text.join(type.displayName, " ", tier.nameSuffix)
    }

    val apiName by lazy {
        if (tier == TrophyTier.NONE) {
            return@lazy type.internalName.lowercase()
        }

        "${type.internalName.lowercase()}_${tier.name.lowercase()}"
    }

    companion object {
        fun fromString(fish: String): TrophyFrog? {
            if (fish.contains("/")) {
                return fish.split("/").let {
                    TrophyFrog(
                        TrophyFrogType.getByInternalName(it[0]) ?: return null,
                        TrophyTier.getByName(it[1]),
                    )
                }
            }
            return null
        }
    }
}
