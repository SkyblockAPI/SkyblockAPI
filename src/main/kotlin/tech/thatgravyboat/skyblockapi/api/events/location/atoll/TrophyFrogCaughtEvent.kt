package tech.thatgravyboat.skyblockapi.api.events.location.atoll

import tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog.TrophyFrogType
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class TrophyFrogCaughtEvent(val type: TrophyFrogType, val tier: TrophyTier) : SkyBlockEvent()
