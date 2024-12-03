package tech.thatgravyboat.skyblockapi.impl.events

import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundTabListPacket
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.chunked
import tech.thatgravyboat.skyblockapi.utils.extentions.peek
import tech.thatgravyboat.skyblockapi.utils.mc.displayName
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

private const val TAB_LIST_LENGTH = 80
private const val CHECK_INTERVAL = 1000

@Module
object TabListEventHandler {

    private val infoRegex = RegexGroup.TABLIST.create(
        "info",
        "(?:Info|Account Info)$"
    )

    private val widgetGroup = RegexGroup.TABLIST_WIDGET

    private val widgetRegexes = mapOf(
        TabWidget.PET to widgetGroup.create("pet", "Pet:"),
        TabWidget.DAILY_QUESTS to widgetGroup.create("daily_quests", "Daily Quests:"),
        TabWidget.FORGES to widgetGroup.create("forges", "Forges:"),
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
        TabWidget.EVENT to widgetGroup.create("event_Trackers", "Event Trackers:"),
        TabWidget.FROZEN_CORPSES to widgetGroup.create("frozen_corpses", "Frozen Corpses:"),

        TabWidget.AREA to widgetGroup.create("area", "Area: (?<area>.*)"),
        TabWidget.PROFILE to widgetGroup.create("profile", "Profile: (?<profile>.*)"),
        TabWidget.ELECTION to widgetGroup.create("election", "Election: (?<election>.*)"),
        TabWidget.EVENT to widgetGroup.create("event", "Event: (?<event>.*)"),
        TabWidget.PARTY to widgetGroup.create("party", "Party: (?<party>.*)"),
        TabWidget.MINIONS to widgetGroup.create("minions", "Minions: (?<party>.*)"),
        TabWidget.SHEN to widgetGroup.create("shen", "Shen: \\((?<duration>[\\ddmsh,]+)\\)"),
        TabWidget.ACTIVE_EFFECTS to widgetGroup.create("active_effects", "Active Effects:(?: \\((?<amount>\\d+)\\))?"),
    )

    private var tabList = emptyList<List<String>>()
    private var lastCheck = 0L

    private var header: Component = CommonComponents.EMPTY
    private var footer: Component = CommonComponents.EMPTY

    private val widgets = mutableMapOf<TabWidget, List<String>>()

    private val lastUnknownTabWidgetAlert = mutableMapOf<String, Long>()

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        this.widgets.clear()
    }

    @Subscription
    fun onTick(event: TickEvent) {
        if (!LocationAPI.isOnSkyBlock) return
        if (System.currentTimeMillis() - lastCheck < CHECK_INTERVAL) return
        lastCheck = System.currentTimeMillis()

        val newTabList = McClient.tablist.take(TAB_LIST_LENGTH).map { it.displayName }.chunked(20)
        val newStringTabList = newTabList.map { it.map { it.stripped } }

        if (tabList != newStringTabList) {
            TabListChangeEvent(tabList, newTabList).post()
            tabList = newStringTabList
        }
    }

    @Subscription
    fun onTabListChange(event: TabListChangeEvent) {
        if (!LocationAPI.isOnSkyBlock) return

        val sections = event.new
            .filter { it.isNotEmpty() && infoRegex.contains(it.first().stripped) }
            .map { it.drop(1) }
            .flatten()
            .chunked { !it.stripped.startsWith(" ") }
            .peek { it.removeIf { c -> c.stripped.isBlank() } }
            .filter { it.isNotEmpty() }

        sections.forEach { section ->
            val title = section.firstOrNull()?.stripped ?: return@forEach
            val widget = widgetRegexes.entries.firstOrNull { it.value.matches(title) }?.key ?: run {
                if ((lastUnknownTabWidgetAlert[title] ?: 0) < System.currentTimeMillis() - 60000) {
                    lastUnknownTabWidgetAlert[title] = System.currentTimeMillis()
                    Logger.debug("Unknown tab widget: $title")
                }
                return@forEach
            }

            val old = widgets[widget] ?: emptyList()
            val new = section.map { it.stripped }
            if (old != new) {
                widgets[widget] = new
                TabWidgetChangeEvent(widget, old, new, section).post()
            }
        }
    }

    @Subscription
    fun onPacketReceived(event: PacketReceivedEvent) {
        if (event.packet is ClientboundTabListPacket) {
            TabListHeaderFooterChangeEvent(
                footer,
                header,
                event.packet.footer,
                event.packet.header,
            ).post(SkyBlockAPI.eventBus)
            this.header = event.packet.header
            this.footer = event.packet.footer
        }
    }
}
