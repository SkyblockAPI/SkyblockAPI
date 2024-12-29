package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines

@Module
object DebugChat {

    private val messages = mutableListOf<Pair<Instant, Component>>()

    @Subscription(priority = Int.MIN_VALUE, receiveCancelled = true)
    fun onMessage(event: ChatReceivedEvent.Pre) {
        messages.add(Clock.System.now() to event.component)
    }

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("chat") {
                callback {
                    McClient.setScreen(DebugChatScreen(messages))
                }
            }
        }
    }
}

private class DebugChatScreen(val messages: List<Pair<Instant, Component>>) : Screen(CommonComponents.EMPTY) {

    private var layout: LinearLayout = LinearLayout.vertical().spacing(2)
    private var scroll = 0

    override fun init() {
        this.layout = LinearLayout.vertical().spacing(2)

        messages.forEach { (timestamp, content) ->
            this.layout.addChild(Widget(timestamp, content))
        }

        this.layout.arrangeElements()
        this.scroll = this.layout.height - this.height
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(graphics, mouseX, mouseY, partialTicks)

        this.layout.visitWidgets {
            it.width = this.width
            it.render(graphics, mouseX, mouseY, partialTicks)

            if (mouseX in it.x until it.x + it.width && mouseY in it.y until it.y + it.height) {
                val tooltip = it.tooltip ?: return@visitWidgets
                setTooltipForNextRenderPass(tooltip, DefaultTooltipPositioner.INSTANCE, true)
            }
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        this.scroll += -scrollY.toInt() * 10
        this.scroll = this.scroll.coerceIn(0, (this.layout.height - this.height).coerceAtLeast(0))
        this.layout.setPosition(0, -this.scroll)
        return true
    }
}

private class Widget(timestamp: Instant, val content: Component) : StringWidget(
    Text.join(
        Text.of("[${timestamp.format(this.formatter)}] : ") {
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

    override fun onClick(d: Double, e: Double) {
        val copyType: String
        if (Screen.hasAltDown()) {
            McClient.clipboard = this.content.toJson(ComponentSerialization.CODEC).toPrettyString()
            copyType = "Component"
        } else if (Screen.hasShiftDown()) {
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

        private val formatter = DateTimeComponents.Format {
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    }

}
