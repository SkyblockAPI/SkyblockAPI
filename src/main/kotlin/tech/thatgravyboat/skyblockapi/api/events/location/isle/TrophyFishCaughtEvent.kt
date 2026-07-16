package tech.thatgravyboat.skyblockapi.api.events.location.isle

import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishTier
import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishType
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class TrophyFishCaughtEvent(val type: TrophyFishType, val tier: TrophyFishTier, val amount: Int = 1) : SkyBlockEvent()
