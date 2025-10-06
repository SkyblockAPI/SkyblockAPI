@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.impl.debug

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.drawFilledBox
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.time.Instant
import kotlin.time.toJavaInstant

internal actual fun DebugChatScreen(messages: List<Pair<Instant, Component>>): Screen = DebugChatScreenImpl(messages)
internal class DebugChatScreenImpl(messages: List<Pair<Instant, Component>>) : Screen(CommonComponents.EMPTY) {

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val toastId = SystemToast.SystemToastId(1500)

    private var scroll = 0
    private val allMessages = messages.asReversed()
    private var filteredMessages = allMessages

    override fun init() {
        super.init()

        val searchBar = addRenderableWidget(EditBox(McFont.self, 100, 16, CommonText.EMPTY))
        searchBar.setHint(Text.of("Search..."))
        searchBar.setResponder { query ->
            scroll = 0
            filteredMessages = if (query.isEmpty()) {
                allMessages
            } else {
                allMessages.filter { it.second.stripped.contains(query, true) }
            }
        }
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderTransparentBackground(graphics)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)

        val status = "Messages: ${filteredMessages.size}"
        graphics.drawString(status, this.width - 5 - McFont.width(status), 2)

        for (index in scroll until min(scroll + 500, filteredMessages.size)) {
            val (timestamp, message) = filteredMessages[index]
            val y = 20 + (index - scroll) * 10

            if (mouseY in y until y + 10) {
                graphics.drawFilledBox(0, y, this.width, 10, 0x80FFFFFF.toInt())
            }
            val instant = LocalDateTime.ofInstant(timestamp.toJavaInstant(), ZoneId.systemDefault())
            graphics.drawString(
                Text.join(Text.of("[${formatter.format(instant)}]: ") { this.color = TextColor.GRAY }, message),
                5, y + 1,
            )
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (mouseY > 20 && button == 1) {
            val index = (mouseY.toInt() - 20) / 10 + scroll
            if (index in filteredMessages.indices) {
                val (_, message) = filteredMessages[index]
                val (content, title) = when {
                    McScreen.isAltDown -> message.toJson(ComponentSerialization.CODEC).toPrettyString() to "Component"
                    McScreen.isShiftDown -> message.splitLines().joinToString { it.toJson(ComponentSerialization.CODEC).toPrettyString() } to "Component Lines"
                    else -> message.string to "String"
                }
                McClient.clipboard = content
                SystemToast.add(
                    McClient.toasts,
                    toastId,
                    Text.of("[SkyBlock API]") { this.color = TextColor.YELLOW },
                    Text.of("Message copied to clipboard! ($title)") { this.color = TextColor.YELLOW },
                )
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
