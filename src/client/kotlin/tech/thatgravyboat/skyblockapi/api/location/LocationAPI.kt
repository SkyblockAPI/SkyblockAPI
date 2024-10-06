package tech.thatgravyboat.skyblockapi.api.location

import net.hypixel.data.type.GameType
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object LocationAPI {

    private val locationRegex = RegexGroup.SCOREBOARD.create(
        "location",
        " *[⏣ф] (?<location>.+)"
    )

    var isOnSkyblock: Boolean = false
        private set

    var island: SkyblockIsland? = null
        private set

    var area: SkyblockArea = SkyBlockAreas.NONE
        private set

    var rawArea = ""
        private set

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        isOnSkyblock = event.type == GameType.SKYBLOCK
        val old = island
        island = if (isOnSkyblock && event.mode != null) {
            SkyblockIsland.getById(event.mode)
        } else {
            null
        }
        IslandChangeEvent(old, island).post(SkyBlockAPI.eventBus)
    }

    @Subscription
    fun onServerDisconnect(event: ServerDisconnectEvent) {
        isOnSkyblock = false
        island = null
    }

    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        if (!isOnSkyblock) return

        event.added.first { locationRegex.matches(it) }.let {
            rawArea = it
            locationRegex.match(it, "location") { (location) ->
                val old = area
                area = SkyblockArea(location)
                AreaChangeEvent(old, area).post(SkyBlockAPI.eventBus)
            }
        }
    }

}
