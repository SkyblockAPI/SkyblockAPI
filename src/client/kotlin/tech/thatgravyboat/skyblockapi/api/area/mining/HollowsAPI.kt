package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object HollowsAPI {
    private val hollowsGroup = RegexGroup.SCOREBOARD.group("mining.hollows")

    // Heat: IMMUNE
    // Heat: 24♨
    private val heatPattern = hollowsGroup.create("heat", "Heat: (?<heat>\\d+|IMMUNE)♨?")

    var heat: Int? = null
        private set

    val immuneToHeat: Boolean get() = heat == null

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (!SkyBlockIsland.CRYSTAL_HOLLOWS.inIsland()) return

        val heatFound = heatPattern.anyMatch(event.added, "heat") { (heat) ->
            this.heat = heat.toIntOrNull()
        }

        if (!heatFound && this.heat != 0 && heatPattern.anyMatch(event.removed)) {
            this.heat = 0
        }
    }

    @Subscription
    fun onIslandSwitch(event: IslandChangeEvent) {
        heat = 0
    }
}
