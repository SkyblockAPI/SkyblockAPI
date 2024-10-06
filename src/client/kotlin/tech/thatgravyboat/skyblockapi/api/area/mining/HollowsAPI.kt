package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object HollowsAPI {
    private val glaciteGroup = RegexGroup.SCOREBOARD.group("mining.hollows")

    var heat: Int? = null
        private set

    var rawHeat = ""
        private set

    // Heat: IMMUNE
    // Heat: 24♨
    private val heatPattern = glaciteGroup.create("heat", "Heat: (?<raw>.*(?<heat>\\d+|IMMUNE)♨?)")

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (!SkyblockIsland.CRYSTAL_HOLLOWS.inIsland()) return

        heatPattern.anyMatch(event.added, "raw", "heat") { (raw, heat) ->
            this.rawHeat = raw
            this.heat = heat.toIntOrNull()
        }
    }
}
