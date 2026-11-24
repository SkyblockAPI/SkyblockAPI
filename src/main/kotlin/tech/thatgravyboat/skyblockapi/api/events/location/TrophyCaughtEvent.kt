package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog.TrophyFrogType
import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishType
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public sealed class TrophyCaughtEvent : SkyBlockEvent() {
    public data class Fish(val type: TrophyFishType, val tier: TrophyTier, val amount: Int = 1) : TrophyCaughtEvent()
    public data class Frog(val type: TrophyFrogType, val tier: TrophyTier, val amount: Int = 1) : TrophyCaughtEvent()
}
