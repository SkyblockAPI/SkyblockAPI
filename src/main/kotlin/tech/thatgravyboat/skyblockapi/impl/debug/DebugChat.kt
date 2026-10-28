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
import tech.thatgravyboat.skyblockapi.utils.devutils.SkyBlockApiDevUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.imitateServerMessage
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.toStringWithFormattingCodes
import java.util.UUID
import kotlin.time.Instant

@Module
public object DebugChat {

    private var sentComponent: Component? = null
        set(value) {
            if (value != null) {
                sentMessage = null
            }
            field = value
        }
    private var sentMessage: UUID? = null
        set(value) {
            if (value != null) {
                sentComponent = null
            }
            field = value
        }

    private data class StoredMessage(
        val original: Component,
        val id: UUID,
        var modified: Component? = null,
        var cancelled: Boolean = false,
    )

    private val chatToastId = SystemToast.SystemToastId(1500)

    // TODO: make this a debugToggle thing (but for ints)
    private val maxMessages = SkyBlockApiDevUtils.getInt("debug_chat_max_size", if (McClient.isDev) 10_000 else 500)
    private val messages = mutableListOf<Pair<Instant, StoredMessage>>()

    private fun getStored(event: ChatReceivedEvent): StoredMessage? = getStored(event.messageId)
    private fun getStored(uuid: UUID?): StoredMessage? = messages.find {
        it.second.id == uuid
    }?.second

    @Subscription(receiveCancelled = true)
    internal fun onMessageFirst(event: ChatReceivedEvent.Pre) {
        if (sentComponent == event.component) {
            sentMessage = event.messageId
        }

        messages.add(currentInstant() to StoredMessage(event.component, event.messageId))
        while (messages.size > maxMessages) messages.removeFirst()
    }

    @Subscription(receiveCancelled = true)
    internal fun onMessageCancelled(event: ChatReceivedEvent.Cancelled) {
        val entry = getStored(event) ?: return
        entry.cancelled = true
    }

    @Subscription(receiveCancelled = true)
    internal fun onMessagePost(event: ChatReceivedEvent.Post) {
        val entry = getStored(event) ?: return
        if (entry.original != event.component) {
            entry.modified = event.component
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

            Text.of {
                append("<< ")
                append(component)
            }.send()
            sentComponent = component
            component.imitateServerMessage()
            val message = getStored(sentMessage)
            sentMessage = null

            if (message == null) return@registerWithCallback
            if (message.cancelled) {
                Text.sendDebug("Chat event cancelled!") {
                    color = TextColor.RED
                }
            }
            if (message.modified != null) {
                Text.sendDebug("Chat event modified!")
            }
        }
    }
}
