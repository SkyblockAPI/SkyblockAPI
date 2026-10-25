package tech.thatgravyboat.skyblockapi.api.location

import me.owdding.ktmodules.Module
import net.hypixel.data.type.GameType
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.FreshHypixelAlphaDetectedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.HypixelJoinEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardTitleUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.location.SkyBlockLocationEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.DebugBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.identifier
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.devutils.SkyBlockApiDevUtils.debugString
import tech.thatgravyboat.skyblockapi.utils.devutils.debugSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.pluralize
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Instant

@Module
public object LocationAPI {

    private val unknownAreasDebug = debugToggle("unknown.areas", "Warns when detecting a new unknown skyblock area")
    private val unknownBiomesDebug = debugToggle("unknown.biomes", "Warns when detecting a new unknown skyblock biome")
    private val unknownAreas = mutableMapOf<String, SkyBlockIsland?>()
    private val unknownBiomes = mutableSetOf<Identifier>()

    private val locationRegex = RegexGroup.SCOREBOARD.create(
        "location",
        " *[⏣ф\uE067\uE020] *(?<location>(?:\\s?[^[ൠ\uE07F\uE018]\\s]+)*)(?: [[ൠ\uE07F\uE018]\uE018] x\\d)?",
    )

    private val guestRegex = RegexGroup.SCOREBOARD.create(
        "guest",
        "^ *\u270C *\\((?<guests>\\d+)/(?<max>\\d+)\\) *$",
    )

    private val playerCountRegex = RegexGroup.TABLIST.create(
        "player_count",
        " *(?:players|party) \\((?<count>\\d+)\\) *",
    )

    private val newAlphaRegex = RegexGroup.CHAT.create(
        "alpha.new",
        "^.>>\\s+Achievement Unlocked: Achievement Get! Hypixel Server!"
    )

    public val forceOnSkyblock: Boolean by debugToggle("location/force_skyblock", "Always returns true for SkyBlock checks")
    public val forceIsland: SkyBlockIsland? by debugSelect<SkyBlockIsland>("location/force_island", "Force a specific island to be returned")
    public val forceOnAlpha: Boolean by debugToggle("location/force_alpha", "Always returns true when checking for onAlpha")

    public var isOnSkyBlock: Boolean = false
        get() = field || forceOnSkyblock
        private set(value) {
            if (field != value) {
                field = value
                if (value) SkyBlockLocationEvent.Join.post() else SkyBlockLocationEvent.Leave.post()
            }
        }

    public var island: SkyBlockIsland? = null
        get() = forceIsland ?: field
        private set

    public var area: SkyBlockArea = SkyBlockAreas.NONE
        private set

    public var biome: SkyBlockBiome? = null
        private set

    public var serverId: String? = null
        private set

    public var isGuest: Boolean = false
        private set

    public var onHypixel: Boolean = false
        private set

    public var onAlpha: Boolean = false
        get() = field || forceOnAlpha
        private set

    public var playerCount: Int = 0
        get() = field.coerceAtLeast(McClient.players.size)
        private set

    public val maxPlayercount: Int?
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
                SkyBlockIsland.DARK_AUCTION -> 12
                else -> 24
            }
        }

    public var lastServerChange: Instant = Instant.DISTANT_PAST
        private set

    @Subscription
    internal fun onServerChange(event: ServerChangeEvent) {
        lastServerChange = currentInstant()
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
    internal fun onHypixelJoin(event: HypixelJoinEvent) {
        onHypixel = true
        onAlpha = event.onAlpha
    }

    @Subscription
    @OnlyOnSkyBlock
    internal fun onTabListUpdate(event: TabListChangeEvent) {
        val component = event.new.firstOrNull()?.firstOrNull() ?: return
        playerCount = playerCountRegex.findGroup(component.stripped.lowercase(), "count")?.toIntOrNull() ?: 0
    }

    @Subscription
    @OnlyOnSkyBlock
    internal fun onScoreboardTitleUpdate(event: ScoreboardTitleUpdateEvent) {
        isGuest = event.new.contains("guest", ignoreCase = true)
    }

    @Subscription
    @OnlyOnSkyBlock
    internal fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        locationRegex.anyMatch(event.added, "location") { (location) ->
            val old = area
            area = SkyBlockArea(location)
            AreaChangeEvent(old, area).post()

            val knownArea = SkyBlockAreas.registeredAreas.entries.find { it.value.name == location } != null
            if (!knownArea) {
                unknownAreas.putIfAbsent(location, island)
                debugString(unknownAreasDebug) { "Unknown area detected: '$location'" }
            }
        }

        guestRegex.anyMatch(event.added, "guests") { (current) ->
            playerCount = current.toIntOrNull() ?: 0
        }
    }

    @Subscription
    internal fun onChatReceivedPre(event: ChatReceivedEvent.Pre) {
        if (!newAlphaRegex.contains(event.text)) return
        FreshHypixelAlphaDetectedEvent.post()
    }


    @OnlyOnSkyBlock
    @Subscription(TickEvent::class)
    internal fun onTick() {
        val pos = McPlayer.self?.blockPosition() ?: return
        val biome = McLevel.selfOrNull?.getBiome(pos)?.unwrapKey()?.getOrNull()?.identifier ?: run {
            LocationAPI.biome = null
            return
        }

        if (biome.namespace != SkyBlockBiomes.HYPIXEL_IDENTIFIER) {
            LocationAPI.biome = null
            return
        }

        val knownBiome = SkyBlockBiomes.registeredBiomes.contains(biome.path)

        val hypixelBiome = SkyBlockBiomes.getSkyBlockBiome(biome)
        if (LocationAPI.biome == hypixelBiome) return

        LocationAPI.biome = hypixelBiome

        if (!knownBiome) {
            unknownBiomes.add(biome)
            debugString(unknownBiomesDebug) { "Unknown biome detected: '$biome'" }
        }
    }

    private fun reset() {
        isOnSkyBlock = false
        serverId = null
        isGuest = false
        area = SkyBlockAreas.NONE
        onHypixel = false
        onAlpha = false
        val old = island
        island = null
        if (old != null) {
            IslandChangeEvent(old, null).post()
        }
    }

    @Subscription(ServerDisconnectEvent::class)
    private fun onServerDisconnect() = reset()

    @ApiDebug("LocationAPI", "location")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        field("island", island?.displayName)
        field("area", area.name)
        field("biome", biome?.biome)
        field(::serverId)
        field("playerCount", "$playerCount${maxPlayercount?.let { "/$it" }.orEmpty()}")
        field(::isGuest)
        field(::onHypixel)
        field(::onAlpha)
    }

    @Subscription
    internal fun onCommand(event: RegisterSkyblockApiCommandsEvent) {
        event.register("unknown") {
            thenCallback("areas") {
                if (unknownAreas.isEmpty()) {
                    Text.sendDebug("No missing areas.")
                    return@thenCallback
                }
                McClient.clipboard = unknownAreas.entries.joinToString(prefix = "[", postfix = "]") { "${it.value?.name ?: "null"} -> ${it.key}" }
                val size = unknownAreas.size
                Text.sendDebug("Copied $size unknown ${pluralize(size, "biome")} to clipboard.")
            }
            thenCallback("biomes") {
                if (unknownBiomes.isEmpty()) {
                    Text.sendDebug("No missing biomes.")
                    return@thenCallback
                }
                McClient.clipboard = unknownBiomes.joinToString(prefix = "[", postfix = "]")
                val size = unknownBiomes.size
                Text.sendDebug("Copied $size unknown ${pluralize(size, "area")} to clipboard.")
            }
        }
        event.registerWithCallback("dev trigger new_alpha") {
            FreshHypixelAlphaDetectedEvent.post()
            Text.sendDebug("Triggered a NewHypixelAlphaDetectedEvent.")
        }
    }
}
