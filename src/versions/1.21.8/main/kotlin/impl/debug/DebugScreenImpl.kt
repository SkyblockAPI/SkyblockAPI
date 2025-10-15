@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.impl.debug

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.platform.drawFilledBox
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.platform.showTooltip
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.time.Instant
import kotlin.time.toJavaInstant

internal actual fun <T> DebugScreen(
    title: String,
    messages: List<Pair<Instant, T>>,
    buttons: List<AbstractWidget>,
    asSearch: (T) -> String,
    display: (T) -> Component,
    tooltip: (T) -> Component,
    onClicked: (T) -> Unit,
    timeFormat: String,
): Screen = DebugScreenImpl(
    title = title,
    messages = messages,
    buttons = buttons,
    asSearch = asSearch,
    display = display,
    tooltip = tooltip,
    onClicked = onClicked,
    timeFormat = timeFormat
)
internal class DebugScreenImpl<T>(
    title: String,
    messages: List<Pair<Instant, T>>,
    val buttons: List<AbstractWidget> = emptyList(),
    val asSearch: (T) -> String,
    val display: (T) -> Component = { Text.of(it.toString()) },
    val tooltip: (T) -> Component = { Text.of("Click to copy to clipboard") { this.color = TextColor.GRAY; } },
    val onClicked: (T) -> Unit,
    val timeFormat: String = "HH:mm:ss",
) : Screen(Text.of(title)) {

    private val timeFormatter = DateTimeFormatter.ofPattern(timeFormat)

    private var scroll = 0
    private var query = ""
    private var queryTime = 0L
    private val allMessages = messages.asReversed()
    private var filteredMessages = allMessages
        get() {
            if (query.isEmpty()) return allMessages
            if (System.currentTimeMillis() - queryTime > 200 && queryTime != 0L) {
                queryTime = 0L
                field = allMessages.parallelStream()
                    .filter { this.asSearch.invoke(it.second).contains(query, true) }
                    .sorted { a, b -> b.first.compareTo(a.first) }
                    .toList()
            }
            return field
        }

    override fun init() {
        super.init()

        scroll = 0

        val searchBar = addRenderableWidget(EditBox(McFont.self, 100, 16, CommonText.EMPTY))
        searchBar.setHint(Text.of("Search..."))
        searchBar.setResponder { query ->
            this.scroll = 0
            this.query = query
            this.queryTime = System.currentTimeMillis()
        }

        val buttons = LinearLayout.horizontal().spacing(4)
        this.buttons.forEach { buttons.addChild(it) }
        buttons.arrangeElements()
        buttons.setPosition(this.width - buttons.width - 5, (20 - buttons.height) / 2)
        buttons.visitWidgets(this::addRenderableWidget)
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderTransparentBackground(graphics)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)

        val status = Text.join(this.title, ": ${filteredMessages.size}")
        graphics.drawString(status, (this.width - McFont.width(status)) / 2, 5)

        val maxEntriesForScreen = (this.height / 10) + 10

        for (index in scroll until min(scroll + maxEntriesForScreen, filteredMessages.size)) {
            val (timestamp, message) = filteredMessages[index]
            val y = 20 + (index - scroll) * 10
            val hovered = mouseY in y until y + 10

            if (hovered) {
                graphics.drawFilledBox(0, y, this.width, 10, 0x80FFFFFF.toInt())
                graphics.showTooltip(this.tooltip.invoke(message))
            }
            val instant = LocalDateTime.ofInstant(timestamp.toJavaInstant(), ZoneId.systemDefault())
            val timeComponent = Text.of("[${timeFormatter.format(instant)}]: ") { this.color = TextColor.GRAY }
            val timeWidth = McFont.width(timeComponent)
            val messageComponent = McFont.self.substrByWidth(this.display.invoke(message), this.width - timeWidth)

            graphics.drawString(timeComponent, 5, y + 1)
            graphics.drawString(messageComponent, 5 + timeWidth, y + 1)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (mouseY > 20 && button == 0) {
            val index = (mouseY.toInt() - 20) / 10 + scroll
            if (index in filteredMessages.indices) {
                val (_, message) = filteredMessages[index]
                this.onClicked.invoke(message)
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double): Boolean {
        if (mouseY > 20) {
            scroll = (scroll - deltaY.toInt()).coerceIn(0, maxOf(0, filteredMessages.size - 1))
            return true
        }
        return false
    }
}
