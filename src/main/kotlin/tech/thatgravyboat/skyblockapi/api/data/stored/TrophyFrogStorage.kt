package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog.TrophyFrogData
import tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog.TrophyFrogType
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.utils.extentions.addOrPut

internal object TrophyFrogStorage {
    private val TROPHY_FROG = StoredProfileData(
        ::TrophyFrogData,
        TrophyFrogData.CODEC,
        "trophy_fish.json",
    )

    fun addCaught(type: TrophyFrogType, tier: TrophyTier) {
        TROPHY_FROG.get()?.data?.getOrPut(type) { mutableMapOf() }?.addOrPut(tier, 1)
        TROPHY_FROG.save()
    }

    fun getCaught(type: TrophyFrogType): Map<TrophyTier, Int> {
        return TROPHY_FROG.get()?.data?.getOrPut(type) { mutableMapOf() } ?: emptyMap()
    }

    fun setAmounts(type: TrophyFrogType, amounts: Map<TrophyTier, Int>) {
        TROPHY_FROG.get()?.data?.getOrPut(type) { mutableMapOf() }?.putAll(amounts)
        TROPHY_FROG.save()
    }

}
