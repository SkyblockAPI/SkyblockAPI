package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyblockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object GlaciteAPI {

    private val glaciteGroup = RegexGroup.SCOREBOARD.group("mining.glacite")

    private val coldRegex = glaciteGroup.create("cold", "Cold: -(?<cold>\\d+)❄")

    var cold: Int = 0
        private set

    fun inGlaciteTunnels() = when {
        SkyblockIsland.MINESHAFT.inIsland() -> true
        SkyblockIsland.DWARVEN_MINES.inIsland() -> SkyblockArea.inAnyArea(
            SkyBlockAreas.GLACITE_TUNNELS,
            SkyBlockAreas.GREAT_LAKE,
            SkyBlockAreas.BASECAMP,
            SkyBlockAreas.FOSSIL_RESEARCH
        )
        else -> false
    }

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        val coldFound = coldRegex.anyMatch(event.added, "cold") { (cold) ->
            this.cold = cold.toIntValue()
        }

        if (!coldFound) {
            coldRegex.anyMatch(event.removed) {
                this.cold = 0
            }
        }
    }
}
