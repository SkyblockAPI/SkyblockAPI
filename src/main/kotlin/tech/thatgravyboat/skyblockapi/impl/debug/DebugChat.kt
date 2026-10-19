package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.gson.JsonParser
import me.owdding.dfu.item.LegacyTextFixer
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.SkyBlockApiDevUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.toStringWithFormattingCodes
import kotlin.time.Instant

@Module
public object DebugChat {

    private data class StoredMessage(
        val original: Component,
        var modified: Component? = null,
        var cancelled: Boolean = false,
    )

    private val chatToastId = SystemToast.SystemToastId(1500)

    // TODO: make this a debugToggle thing (but for ints)
    private val maxMessages = SkyBlockApiDevUtils.getInt("debug_chat_max_size", if (McClient.isDev) 10_000 else 500)
    private val messages = mutableListOf<Pair<Instant, StoredMessage>>()

    @Subscription(priority = Int.MIN_VALUE, receiveCancelled = true)
    internal fun onMessageFirst(event: ChatReceivedEvent.Pre) {
        messages.add(currentInstant() to StoredMessage(event.component))
        while (messages.size > maxMessages) messages.removeFirst()
    }

    @Subscription(priority = Int.MAX_VALUE, receiveCancelled = true)
    internal fun onMessageLast(event: ChatReceivedEvent.Pre) {
        val last = messages.lastOrNull()?.second ?: return
        last.cancelled = event.isCancelled
        if (last.original != event.component) {
            last.modified = event.component
        }
    }

    @Subscription(priority = Int.MAX_VALUE)
    internal fun onMessagePostLast(event: ChatReceivedEvent.Post) {
        val last = messages.lastOrNull()?.second ?: return
        if (last.original != event.component) {
            last.modified = event.component
        }
    }

    @Subscription
    internal fun onCommandsRegistration(event: RegisterSkyblockApiCommandsEvent) {
        event.registerWithCallback("chat") {
            val screen = DebugScreen(
                "Messages",
                messages,
                asSearch = { it.original.stripped + (it.modified?.stripped ?: "") },
                display = { chat ->
                    val original = chat.original
                    val current = chat.modified ?: chat.original

                    Text.join(
                        if (chat.cancelled) Text.of("❌", TextColor.RED) else null,
                        if (chat.modified != null) Text.of("✎", TextColor.YELLOW) else null,
                        if (chat.modified != null) Text.join(original, Text.of("->", TextColor.GRAY), current, separator = CommonText.SPACE) else current,
                        separator = CommonText.SPACE,
                    )
                },
                tooltip = {
                    Text.multiline(
                        "Click to copy the String",
                        "Alt-Click to copy the Component",
                        "Shift-Click to copy the components split by lines",
                        "Ctrl-Click to copy the Legacy Codes String",
                    )
                },
                onClicked = { chat ->
                    val message = chat.modified ?: chat.original
                    val (content, title) = when {
                        McScreen.isAltDown -> message.toJson(ComponentSerialization.CODEC).toPrettyString() to "Component"
                        McScreen.isShiftDown -> message.splitLines()
                            .joinToString { it.toJson(ComponentSerialization.CODEC).toPrettyString() } to "Component Lines"

                        McScreen.isControlDown -> message.toStringWithFormattingCodes() to "Legacy Codes"
                        else -> message.string to "String"
                    }
                    McClient.clipboard = content
                    SystemToast.add(
                        McClient.toasts,
                        chatToastId,
                        CommonText.PREFIX,
                        Text.of("Message copied to clipboard! ($title)", TextColor.YELLOW),
                    )
                },
            )
            McClient.setScreen(screen)
        }
        event.registerWithCallback("message") {
            val clipboard = McClient.clipboard.takeUnless { it.isEmpty() } ?: return@registerWithCallback
            val component = runCatching {
                JsonParser.parseString(clipboard).toData(ComponentSerialization.CODEC)
            }.getOrNull() ?: LegacyTextFixer.parse(clipboard)

            if (ChatReceivedEvent.Pre(component).post()) {
                Text.sendDebug("Chat event cancelled!") {
                    color = TextColor.RED
                }
                component.send()
            } else {
                val event = ChatReceivedEvent.Post(component)
                event.post()
                if (event.component != component) {
                    Text.sendDebug("Chat event modified!")
                }
                event.component.send()
            }
        }
    }
}
