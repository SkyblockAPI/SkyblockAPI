package tech.thatgravyboat.skyblockapi.impl.events

import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundTabListPacket
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.clearAnd
import tech.thatgravyboat.skyblockapi.utils.extentions.enumMapOf
import tech.thatgravyboat.skyblockapi.utils.mc.displayName
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import tech.thatgravyboat.skyblockapi.utils.time.currentInstant
import tech.thatgravyboat.skyblockapi.utils.time.since
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

private const val TAB_LIST_LENGTH = 80
private const val TAB_LIST_SECTION = 20

@Module
internal object TabListEventHandler {

    private val infoRegex = RegexGroup.TABLIST.create(
        "info",
        "(?:Info|Account Info|Player Stats|Dungeon Stats)$",
    )

    private val widgetGroup = RegexGroup.TABLIST_WIDGET

    private val widgetRegexes = mapOf(
        TabWidget.PET to widgetGroup.create("pet", "Pet:"),
        TabWidget.DAILY_QUESTS to widgetGroup.create("daily_quests", "Daily Quests:"),
        TabWidget.FORGES to widgetGroup.create("forges", "Forges:(?: \\((?<active>[\\d,.]+)/(?<max>[\\d,.]+)\\))?"),
        TabWidget.COMMISSIONS to widgetGroup.create("commissions", "Commissions:"),
        TabWidget.SKILLS to widgetGroup.create("skills", "Skills:(?: (?<avg>[\\d.]+))?"),
        TabWidget.POWDERS to widgetGroup.create("powders", "Powders:"),
        TabWidget.CRYSTALS to widgetGroup.create("crystals", "Crystals:"),
        TabWidget.BESTIARY to widgetGroup.create("bestiary", "Bestiary:"),
        TabWidget.COLLECTION to widgetGroup.create("collection", "Collection:"),
        TabWidget.STATS to widgetGroup.create("stats", "Stats:"),
        TabWidget.DUNGEONS to widgetGroup.create("dungeons", "Dungeons:"),
        TabWidget.ESSENCE to widgetGroup.create("essence", "Essence:"),
        TabWidget.GOOD_TO_KNOW to widgetGroup.create("good_to_know", "Good to know:"),
        TabWidget.ADVERTISEMENT to widgetGroup.create("advertisement", "Advertisement:"),
        TabWidget.TRAPPER to widgetGroup.create("trapper", "Trapper:"),
        TabWidget.EVENT_TRACKERS to widgetGroup.create("event_Trackers", "Event Trackers:"),
        TabWidget.FROZEN_CORPSES to widgetGroup.create("frozen_corpses", "Frozen Corpses:"),
        TabWidget.AREA to widgetGroup.create("area", "(?:Area|Dungeon): (?<area>.*)"),
        TabWidget.PROFILE to widgetGroup.create("profile", "Profile: (?<profile>.*)"),
        TabWidget.ELECTION to widgetGroup.create("election", "Election: (?<election>.*)"),
        TabWidget.EVENT to widgetGroup.create("event", "Event: (?<event>.*)"),
        TabWidget.PARTY to widgetGroup.create("party", "Party: (?<party>.*)"),
        TabWidget.MINIONS to widgetGroup.create("minions", "Minions: (?<party>.*)"),
        TabWidget.SHEN to widgetGroup.create("shen", "Shen: \\((?<duration>[\\ddmsh,]+)\\)"),
        TabWidget.ACTIVE_EFFECTS to widgetGroup.create("active_effects", "Active Effects:(?: \\((?<amount>\\d+)\\))?"),
        TabWidget.MINING_EVENT to widgetGroup.create("mining_event", "Mining Event: (?<event>.*)"),
        TabWidget.TIMERS to widgetGroup.create("timers", "Timers:"),
        TabWidget.COMPOSTER to widgetGroup.create("composter", "Composter:"),
        TabWidget.JACOBS_CONTEST to widgetGroup.create("jacobs_contest", "Jacob's Contest:(?: (?<time>.*))?"),
        TabWidget.PESTS to widgetGroup.create("pets", "Pests:"),
        TabWidget.PEST_TRAPS to widgetGroup.create("pest_traps", "Pest Traps: (?<amount>[\\d,.]+)/(?<max>[\\d,.]+)"),
        TabWidget.VISITORS to widgetGroup.create("visitors", "Visitors: \\((?<amount>\\d+)\\)"),
        TabWidget.RNG_METER to widgetGroup.create("rng_meter", "RNG Meter"),
        TabWidget.DOWNED to widgetGroup.create("downed", "Downed: (?<status>.*)"),
        TabWidget.TEAM_DEATHS to widgetGroup.create("team_deaths", "Team Deaths: (?<amount>\\d+)"),
        TabWidget.DISCOVERIES to widgetGroup.create("discoveries", "Discoveries: (?<amount>\\d+)"),
        TabWidget.PUZZLES to widgetGroup.create("puzzles", "Puzzles: \\((?<amount>\\d+)\\)"),
        TabWidget.REPUTATION to widgetGroup.create("reputation", "(?:Mage|Barbarian) Reputation:"),
        TabWidget.TROPHY_FISH to widgetGroup.create("trophy_fish", "Trophy Fish:"),
        TabWidget.FACTION_QUESTS to widgetGroup.create("faction_quests", "Faction Quests:"),
        TabWidget.FOREST_WHISPERS to widgetGroup.create("forest_whispers", "Forest Whispers: (?<amount>[\\dkmb,.]+)"),
        TabWidget.MOONGLADE_BEACON to widgetGroup.create("moonglade_beacon", "Moonglade Beacon: (?<amount>[\\d,.]+) Stacks?"),
        TabWidget.FIRE_SALE to widgetGroup.create("fire_sale", "Fire Sales: \\((?<amount>[\\d,.]+)\\)"),
    )

    private val debug by debugToggle("tab_widget", "Sends a debug message when an unknown tab widget is found.")

    private var tabList = emptyList<List<String>>()

    private var header: Component = CommonText.EMPTY
    private var footer: Component = CommonText.EMPTY

    internal val widgets = enumMapOf<TabWidget, List<String>>()

    private val lastUnknownTabWidgetAlert = mutableMapOf<String, Instant>()

    @Subscription(ServerChangeEvent::class)
    fun onServerChange() {
        widgets.entries.clearAnd { (widget, old) ->
            TabWidgetChangeEvent(widget, old, emptyList(), emptyList()).post()
        }
    }

    @Subscription(TickEvent::class)
    @OnlyOnSkyBlock
    @TimePassed("1s")
    fun onTick() {
        val newTabList = McClient.tablist.take(TAB_LIST_LENGTH).map { it.displayName }.chunked(TAB_LIST_SECTION)
        val newStringTabList = newTabList.map { list -> list.map { it.stripped } }

        if (tabList != newStringTabList) {
            TabListChangeEvent(tabList, newTabList).post()
            tabList = newStringTabList
        }
    }

    @Subscription
    fun onTabListChange(event: TabListChangeEvent) {
        val lines = event.new
            .filter { it.isNotEmpty() && infoRegex.contains(it.first().stripped) }
            .map { it.drop(1) }
            .flatten()

        val widgetLines = mutableMapOf<TabWidget, List<Component>>()
        val currentComponents = mutableListOf<Component>()
        var currentWidget: TabWidget? = null

        fun flushWidget() {
            if (currentWidget != null && currentComponents.isNotEmpty()) {
                widgetLines[currentWidget!!] = currentComponents.toList()
                currentComponents.clear()
            }
        }

        for (line in lines) {
            val stripped = line.stripped
            if (stripped.isBlank()) continue

            val widget = widgetRegexes.entries.find { it.value.matches(stripped) }?.key
            if (widget == null) {
                if (couldBeUnknownWidgetStart(currentWidget, stripped)) {
                    handleUnknownWidget(stripped)
                }
                currentComponents.add(line)
                continue
            }
            flushWidget()
            currentComponents.add(line)
            currentWidget = widget
        }
        flushWidget()

        widgetLines.forEach { (widget, section) ->
            val old = widgets[widget] ?: emptyList()
            val new = section.map { it.stripped }
            if (old != new) {
                widgets[widget] = new
                TabWidgetChangeEvent(widget, old, new, section).post()
            }
        }

        // If a tab widget is stored, but it isn't in the newly detected ones, it means it doesn't exist anymore
        widgets.keys.toSet().subtract(widgetLines.keys).forEach { widget ->
            val removed = widgets.remove(widget) ?: return@forEach
            TabWidgetChangeEvent(widget, removed, emptyList(), emptyList()).post()
        }
    }

    // Add exceptions to widget lines that don't start with a space here
    private fun couldBeUnknownWidgetStart(currentWidget: TabWidget? = null, string: String): Boolean {
        if (string.startsWith(" ")) return false
        return when (currentWidget) {
            TabWidget.JACOBS_CONTEST -> string != "ACTIVE"
            TabWidget.MINING_EVENT -> !string.startsWith("Ends in: ")
            else -> true
        }
    }

    private fun handleUnknownWidget(string: String) {
        val lastAlert = lastUnknownTabWidgetAlert[string]?.since()
        if (lastAlert != null && lastAlert < 1.minutes) return
        val recentWorldChange = LocationAPI.lastServerChange.since() < 2.5.seconds
        if (SkyBlockAPI.isDebug || (debug && !recentWorldChange)) {
            lastUnknownTabWidgetAlert[string] = currentInstant()
            Text.sendDebug("Unknown tab widget: $string") {
                this.color = TextColor.RED
                if (recentWorldChange) append(" (Probably due to world change)", TextColor.RED)
                hover = Text.of("Click to copy all unknown tab widgets to clipboard", TextColor.YELLOW)
                onClick {
                    McClient.clipboard = lastUnknownTabWidgetAlert.keys.toString()
                    Text.sendDebug("Copied all unknown tab widgets to clipboard.")
                }
            }
        }
    }

    @Subscription
    fun onPacketReceived(event: PacketReceivedEvent) {
        if (event.packet is ClientboundTabListPacket) {
            TabListHeaderFooterChangeEvent(
                footer,
                header,
                event.packet.footer(),
                event.packet.header(),
            ).post(SkyBlockAPI.eventBus)
            this.header = event.packet.header()
            this.footer = event.packet.footer()
        }
    }
}
