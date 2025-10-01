@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant

internal actual fun DebugChatScreen(messages: List<Pair<Instant, Component>>): Screen = DebugChatScreenImpl(messages)
internal class DebugChatScreenImpl(val messages: List<Pair<Instant, Component>>) : Screen(CommonComponents.EMPTY) {

    private var layout: LinearLayout = LinearLayout.vertical().spacing(2)
    private var scroll = 0

    override fun init() {
        this.layout = LinearLayout.vertical().spacing(2)

        messages.forEach { (timestamp, content) ->
            this.layout.addChild(Widget(timestamp, content))
        }

        this.layout.arrangeElements()
        this.scroll = this.layout.height - this.height
        updateScroll()
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderTransparentBackground(graphics)

        this.layout.visitWidgets {
            it.width = this.width
            it.render(graphics, mouseX, mouseY, partialTicks)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            this.layout.visitWidgets {
                if (mouseX.toInt() in it.x until it.x + it.width && mouseY.toInt() in it.y until it.y + it.height) {
                    it.onClick(mouseX, mouseY)
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    fun updateScroll() {
        this.scroll = this.scroll.coerceIn(0, (this.layout.height - this.height).coerceAtLeast(0))
        this.layout.setPosition(0, -this.scroll)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        this.scroll += -scrollY.toInt() * 10
        updateScroll()
        return true
    }
}

private class Widget(timestamp: Instant, val content: Component) : StringWidget(
    Text.join(
        Text.of("[${this.formatter.format(LocalDateTime.ofInstant(timestamp.toJavaInstant(), ZoneId.systemDefault()))}] : ") {
            this.color = TextColor.DARK_GRAY
        },
        content,
    ),
    McFont.self,
) {

    init {
        alignLeft()
        tooltip = Tooltip.create(this.content)
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (this.isHoveredOrFocused) {
            graphics.fill(this.x - 1, this.y - 1, this.x + this.width + 2, this.y + this.height + 1, 0x50DDDDDD)
        }
        super.renderWidget(graphics, mouseX, mouseY, partialTicks)
    }

    override fun onClick(d: Double, e: Double) {
        val copyType: String
        if (McScreen.isAltDown) {
            McClient.clipboard = this.content.toJson(ComponentSerialization.CODEC).toPrettyString()
            copyType = "Component"
        } else if (McScreen.isShiftDown) {
            val lines = this.content.splitLines()
            McClient.clipboard = lines.joinToString { it.toJson(ComponentSerialization.CODEC).toPrettyString() }
            copyType = "Component Lines"
        } else {
            McClient.clipboard = this.content.string
            copyType = "String"
        }
        SystemToast.add(
            McClient.toasts,
            toastId,
            Text.of("[SkyBlock API]") {
                this.color = TextColor.YELLOW
            },
            Text.of("Message copied to clipboard! ($copyType)") {
                this.color = TextColor.YELLOW
            },
        )
    }

    companion object {

        private val toastId = SystemToastId(1500)

        private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    }

}
