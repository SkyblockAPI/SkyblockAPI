package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object HollowsAPI {
    private val glaciteGroup = RegexGroup.SCOREBOARD.group("mining.hollows")

    var heat: Int? = null
        private set

    val immuneToHeat: Boolean get() = heat == null

    // Heat: IMMUNE
    // Heat: 24♨
    private val heatPattern = glaciteGroup.create("heat", "Heat: (?<heat>\\d+|IMMUNE)♨?")

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (!SkyblockIsland.CRYSTAL_HOLLOWS.inIsland()) return

        heatPattern.anyMatch(event.new, "heat") { (heat) ->
            this.heat = heat.toIntOrNull()
            return@anyMatch
        }

        heatPattern.anyMatch(event.old, "heat") { (heat) ->
            val oldHeat = heat.toIntOrNull()

            // Heat doesn't show up on the scoreboard if its 0,
            // so we need to check if it was removed and set it to 0
            if (oldHeat == 1 && this.heat != 2) {
                this.heat = 0
                println("Heat was removed")
            }
        }
    }

    @Subscription
    fun onIslandSwitch(event: IslandChangeEvent) {
        heat = 0
    }
}
