package tech.thatgravyboat.skyblockapi.impl.events

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.chunked
import tech.thatgravyboat.skyblockapi.utils.extentions.peek
import tech.thatgravyboat.skyblockapi.utils.mc.displayName
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

private const val TAB_LIST_LENGTH = 80
private const val CHECK_INTERVAL = 1000

@Module
object TabListEventHandler {

    private val infoRegex = Regexes.create(
        "tablist.info",
        ".*(?:Info|Account Info)"
    )

    private val widgetRegexes = mapOf(
        TabWidget.PET to Regexes.create("tablist.widget.pet", "Pet:"),
        TabWidget.DAILY_QUESTS to Regexes.create("tablist.widget.daily_quests", "Daily Quests:"),
        TabWidget.FORGES to Regexes.create("tablist.widget.forges", "Forges:"),
        TabWidget.COMMISSIONS to Regexes.create("tablist.widget.commissions", "Commissions:"),
        TabWidget.SKILLS to Regexes.create("tablist.widget.skills", "Skills:"),
        TabWidget.POWDERS to Regexes.create("tablist.widget.powders", "Powders:"),
        TabWidget.CRYSTALS to Regexes.create("tablist.widget.crystals", "Crystals:"),
        TabWidget.BESTIARY to Regexes.create("tablist.widget.bestiary", "Bestiary:"),
        TabWidget.COLLECTION to Regexes.create("tablist.widget.collection", "Collection:"),
        TabWidget.STATS to Regexes.create("tablist.widget.stats", "Stats:"),
        TabWidget.DUNGEONS to Regexes.create("tablist.widget.dungeons", "Dungeons:"),
        TabWidget.ESSENCE to Regexes.create("tablist.widget.essence", "Essence:"),

        TabWidget.AREA to Regexes.create("tablist.widget.area", "Area: (?<area>.*)"),
        TabWidget.PROFILE to Regexes.create("tablist.widget.profile", "Profile: (?<profile>.*)"),
        TabWidget.ELECTION to Regexes.create("tablist.widget.election", "Election: (?<election>.*)"),
        TabWidget.EVENT to Regexes.create("tablist.widget.event", "Event: (?<event>.*)"),
        TabWidget.PARTY to Regexes.create("tablist.widget.party", "Party: (?<party>.*)"),
        TabWidget.MINIONS to Regexes.create("tablist.widget.party", "Minions: (?<party>.*)"),
    )

    private var tabList = emptyList<List<String>>()
    private var lastCheck = 0L

    private val tabWidgets = mutableMapOf<TabWidget, List<String>>()

    @Subscription
    fun onTick(event: TickEvent) {
        if (System.currentTimeMillis() - lastCheck < CHECK_INTERVAL) return
        lastCheck = System.currentTimeMillis()

        val newTabList = McClient.tablist
            .take(TAB_LIST_LENGTH)
            .map { it.displayName.stripped }
            .chunked(20)

        if (tabList != newTabList) {
            TabListChangeEvent(tabList, newTabList).post(SkyBlockAPI.eventBus)
            tabList = newTabList
        }
    }

    @Subscription
    fun onTabListChange(event: TabListChangeEvent) {
        if (!LocationAPI.isOnSkyblock) return

        for (column in event.new) {
            if (column.isEmpty() || infoRegex.matches(column.first())) continue

            val sections = column
                .drop(1)
                .chunked { !it.startsWith(" ") }
                .peek { it.removeIf(CharSequence::isBlank) }
                .filter { it.isNotEmpty() }

            sections.forEach { section ->
                val title = section.firstOrNull() ?: return@forEach
                val widget = widgetRegexes.entries.firstOrNull { it.value.matches(title) }?.key
                    ?: return@forEach Logger.debug("Unknown tab widget: $title")

                val old = tabWidgets[widget] ?: emptyList()
                if (old != section) {
                    tabWidgets[widget] = section
                    TabWidgetChangeEvent(widget, old, section).post(SkyBlockAPI.eventBus)
                }
            }
        }
    }
}
