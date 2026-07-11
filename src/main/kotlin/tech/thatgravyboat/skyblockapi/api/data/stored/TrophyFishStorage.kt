package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishData
import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishType
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.TrophyTier
import tech.thatgravyboat.skyblockapi.utils.extentions.addOrPut

internal object TrophyFishStorage {
    private val TROPHY_FISH = StoredProfileData(
        ::TrophyFishData,
        TrophyFishData.CODEC,
        "trophy_fish.json",
    )

    fun addCaught(type: TrophyFishType, tier: TrophyTier) {
        TROPHY_FISH.get()?.data?.getOrPut(type) { mutableMapOf() }?.addOrPut(tier, 1)
        TROPHY_FISH.save()
    }

    fun getCaught(type: TrophyFishType): Map<TrophyTier, Int> {
        return TROPHY_FISH.get()?.data?.getOrPut(type) { mutableMapOf() } ?: emptyMap()
    }

    fun setAmounts(type: TrophyFishType, amounts: Map<TrophyTier, Int>) {
        TROPHY_FISH.get()?.data?.getOrPut(type) { mutableMapOf() }?.putAll(amounts)
        TROPHY_FISH.save()
    }

}
