package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object GlaciteAPI {

    private val glaciteGroup = RegexGroup.SCOREBOARD.group("mining.glacite")

    private val coldRegex = glaciteGroup.create("cold", "Cold: -(?<cold>\\d+)❄")

    private val locationRegex = glaciteGroup.create("location", "Glacite Tunnels|Great Glacite Lake")

    var cold: Int = 0
        private set

    fun inGlaciteTunnels() =
        SkyblockIsland.MINESHAFT.inIsland() || (SkyblockIsland.DWARVEN_MINES.inIsland() && locationRegex.match(LocationAPI.area.name))

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        coldRegex.anyMatch(event.added, "cold") { (cold) ->
            this.cold = cold.toIntValue()
        }
    }
}
