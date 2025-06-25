package tech.thatgravyboat.skyblockapi.api.location

import me.owdding.ktmodules.Module
import net.hypixel.data.type.GameType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardTitleUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findOrNull
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object LocationAPI {

    private val unknownIslands = mutableMapOf<String, SkyBlockIsland?>()
    private var sendUnknownChatMessage = false

    private val locationRegex = RegexGroup.SCOREBOARD.create(
        "location",
        " *[⏣ф] *(?<location>(?:\\s?[^ൠ\\s]+)*)(?: ൠ x\\d)?",
    )

    private val guestRegex = RegexGroup.SCOREBOARD.create(
        "guest",
        "^ *\u270C *\\((?<guests>\\d+)/(?<max>\\d+)\\) *$",
    )

    private val playerCountRegex = RegexGroup.TABLIST.create(
        "player_count",
        " *(?:players|party) \\((?<count>\\d+)\\) *",
    )

    var isOnSkyBlock: Boolean = false
        private set

    var island: SkyBlockIsland? = null
        private set

    var area: SkyBlockArea = SkyBlockAreas.NONE
        private set

    var serverId: String? = null
        private set

    var isGuest: Boolean = false
        private set

    var playerCount: Int = 0
        get() = field.coerceAtLeast(McClient.players.size)
        private set

    val maxPlayercount: Int?
        get() = when {
            serverId?.startsWith("mega") == true -> 60
            else -> when (island) {
                SkyBlockIsland.PRIVATE_ISLAND, SkyBlockIsland.GARDEN -> null
                SkyBlockIsland.KUUDRA -> 4
                SkyBlockIsland.MINESHAFT -> 4
                SkyBlockIsland.THE_CATACOMBS -> 5
                SkyBlockIsland.BACKWATER_BAYOU -> 16
                SkyBlockIsland.HUB -> 26
                SkyBlockIsland.JERRYS_WORKSHOP -> 27
                SkyBlockIsland.DARK_AUCTION -> 30
                else -> 24
            }
        }

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

        serverId = event.name
    }

    @Subscription
    fun onTabListUpdate(event: TabListChangeEvent) {
        if (!isOnSkyBlock) return
        val component = event.new.firstOrNull()?.firstOrNull() ?: return
        playerCount = playerCountRegex.findOrNull(component.stripped.lowercase(), "count") { (count) -> count.toIntOrNull() } ?: 0
    }

    @Subscription
    fun onScoreboardTitleUpdate(event: ScoreboardTitleUpdateEvent) {
        if (!isOnSkyBlock) return

        isGuest = event.new.contains("guest", ignoreCase = true)
    }

    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        if (!isOnSkyBlock) return
        locationRegex.anyMatch(event.added, "location") { (location) ->
            val old = area
            area = SkyBlockArea(location)
            AreaChangeEvent(old, area).post()

            val knownArea = SkyBlockAreas.registeredAreas.entries.find { it.value.name == location } != null
            if (!knownArea) {
                unknownIslands.putIfAbsent(location, island)
                if (sendUnknownChatMessage) {
                    Text.of("Unknown area detected: $location").send()
                }
            }
        }

        guestRegex.anyMatch(event.added, "guests", "max") { (current, _) ->
            playerCount = current.toIntOrNull() ?: 0
        }
    }

    private fun reset() {
        isOnSkyBlock = false
        island = null
    }

    @Subscription
    fun onServerDisconnect(event: ServerDisconnectEvent) = reset()

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.registerWithCallback("sbapi unknownareas") {
            McClient.clipboard = unknownIslands.entries.joinToString("\n") { "${it.value?.name ?: "null"} -> ${it.key}" }
            Text.of("Copied ${unknownIslands.size} unknown areas to clipboard!").send()
            sendUnknownChatMessage != sendUnknownChatMessage
        }
        event.registerWithCallback("sbapi location") {
            Text.multiline(
                "Island: ${island?.displayName ?: "Unknown"}",
                "Area: ${area.name}",
                "Server ID: ${serverId ?: "Unknown"}",
                "Player Count: $playerCount${maxPlayercount?.let { " / $it" } ?: ""}",
                "Is Guest: $isGuest",
            ).send()
        }
    }
}
