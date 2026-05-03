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
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.enumMapOf
import tech.thatgravyboat.skyblockapi.utils.extentions.since
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

    private val widgetRegexes = TabWidget.entries.associateWith { it.regex }

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
        TabListChangeEvent(tabList, emptyList()).post()
        tabList = emptyList()
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
            val oldHeader = this.header
            val oldFooter = this.footer
            this.header = event.packet.header()
            this.footer = event.packet.footer()

            McClient.runNextTick {
                TabListHeaderFooterChangeEvent(oldFooter, oldHeader, this.footer, this.header).post(SkyBlockAPI.eventBus)
            }
        }
    }
}
