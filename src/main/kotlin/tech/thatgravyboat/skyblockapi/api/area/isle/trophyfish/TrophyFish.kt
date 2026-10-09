package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.utils.text.Text

public data class TrophyFish(val type: TrophyFishType, @get:JvmName("getTrophyTier") val tier: TrophyTier) {

    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    @Suppress("DEPRECATION")
    public constructor(type: TrophyFishType, tier: TrophyFishTier) : this(type, TrophyTier.valueOf(tier.name))

    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    @Suppress("DEPRECATION")
    @JvmName("getTier")
    public fun getOldTier(): TrophyFishTier = TrophyFishTier.valueOf(tier.name)

    val item: ItemStack by lazy { type.getItem(tier) }
    val displayName: Component by lazy {
        if (tier == TrophyTier.NONE) {
            return@lazy type.displayName.copy()
        }

        Text.join(type.displayName, " ", tier.nameSuffix)
    }

    public val apiName: String by lazy {
        if (tier == TrophyTier.NONE) {
            return@lazy type.internalName.lowercase()
        }

        "${type.internalName.lowercase()}_${tier.name.lowercase()}"
    }

    public companion object {
        public fun fromString(fish: String): TrophyFish? {
            if (fish.contains("/")) {
                return fish.split("/").let {
                    TrophyFish(
                        TrophyFishType.getByInternalName(it[0]) ?: return null,
                        TrophyTier.getByName(it[1]),
                    )
                }
            }
            return null
        }
    }
}
