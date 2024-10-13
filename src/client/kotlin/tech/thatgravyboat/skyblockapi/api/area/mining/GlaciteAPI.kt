package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object GlaciteAPI {

    private val scoreboardGroup = RegexGroup.SCOREBOARD.group("mining.glacite")

    private val coldRegex = scoreboardGroup.create(
        "cold",
        "Cold: -(?<cold>\\d+)❄"
    )

    var cold: Int = 0
        private set

    fun inGlaciteTunnels() = when {
        SkyBlockIsland.MINESHAFT.inIsland() -> true
        SkyBlockIsland.DWARVEN_MINES.inIsland() -> SkyBlockArea.inAnyArea(
            SkyBlockAreas.GLACITE_TUNNELS,
            SkyBlockAreas.GREAT_LAKE,
            SkyBlockAreas.BASECAMP,
            SkyBlockAreas.FOSSIL_RESEARCH,
        )
        else -> false
    }

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        val coldFound = coldRegex.anyMatch(event.added, "cold") { (cold) ->
            this.cold = cold.toIntValue()
        }

        if (!coldFound && this.cold != 0 && coldRegex.anyMatch(event.removed)) {
            this.cold = 0
        }
    }

    private fun reset() {
        cold = 0
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) = reset()
}
