package tech.thatgravyboat.skyblockapi.api.location

import net.hypixel.data.type.GameType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object LocationAPI {

    private val locationRegex = RegexGroup.SCOREBOARD.create(
        "location",
        " *[⏣ф] *(?<location>(?:\\s?[^ൠ\\s]+)*)(?: ൠ x\\d)?",
    )

    var isOnSkyBlock: Boolean = false
        private set

    var island: SkyBlockIsland? = null
        private set

    var area: SkyBlockArea = SkyBlockAreas.NONE
        private set

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        isOnSkyBlock = event.type == GameType.SKYBLOCK
        val old = island
        island = if (isOnSkyBlock && event.mode != null) {
            SkyBlockIsland.getById(event.mode)
        } else {
            null
        }
        IslandChangeEvent(old, island).post()
    }

    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        if (!isOnSkyBlock) return
        locationRegex.anyMatch(event.added, "location") { (location) ->
            val old = area
            area = SkyBlockArea(location)
            AreaChangeEvent(old, area).post()
        }
    }

    private fun reset() {
        isOnSkyBlock = false
        island = null
    }

    @Subscription
    fun onServerDisconnect(event: ServerDisconnectEvent) = reset()
}
