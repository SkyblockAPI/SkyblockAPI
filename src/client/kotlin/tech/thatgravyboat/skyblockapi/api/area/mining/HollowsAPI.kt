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

    var heatImmunity: Boolean = false
        get() = heat == null

    // Heat: IMMUNE
    // Heat: 24♨
    private val heatPattern = glaciteGroup.create("heat", "Heat: (?<heat>\\d+|IMMUNE)♨?")

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (!SkyblockIsland.CRYSTAL_HOLLOWS.inIsland()) return

        heatPattern.anyMatch(event.added, "heat") { (heat) ->
            this.heat = heat.toIntOrNull()
        } else {
            // Heat doesnt show up on the scoreboard if its 0,
            // so we need to check if it was removed and set it to 0
            heat = 0
        }
    }
}
