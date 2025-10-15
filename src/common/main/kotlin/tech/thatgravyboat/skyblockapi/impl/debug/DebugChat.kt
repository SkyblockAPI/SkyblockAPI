package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.gson.JsonParser
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.SkyBlockApiDevUtils
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import kotlin.time.Clock
import kotlin.time.Instant

@Module
object DebugChat {

    private val chatToastId = SystemToast.SystemToastId(1500)
    private val maxMessages = SkyBlockApiDevUtils.getInt("debug_chat_max_size", if (McClient.isDev) 10_000 else 500)
    private val messages = mutableListOf<Pair<Instant, Component>>()

    @Subscription(priority = Int.MIN_VALUE, receiveCancelled = true)
    fun onMessage(event: ChatReceivedEvent.Pre) {
        messages.add(Clock.System.now() to event.component)
        while(messages.size > maxMessages) messages.removeFirst()
    }

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.registerWithCallback("sbapi chat") {
            val screen = DebugScreen(
                "Messages",
                messages,
                asSearch = { it.stripped },
                display = { it },
                onClicked = { message ->
                    val (content, title) = when {
                        McScreen.isAltDown -> message.toJson(ComponentSerialization.CODEC).toPrettyString() to "Component"
                        McScreen.isShiftDown -> message.splitLines().joinToString { it.toJson(ComponentSerialization.CODEC).toPrettyString() } to "Component Lines"
                        else -> message.string to "String"
                    }
                    McClient.clipboard = content
                    SystemToast.add(
                        McClient.toasts,
                        chatToastId,
                        Text.of("[SkyBlock API]") { this.color = TextColor.YELLOW },
                        Text.of("Message copied to clipboard! ($title)") { this.color = TextColor.YELLOW },
                    )
                }
            )
            McClient.setScreen(screen)
        }
        event.registerWithCallback("sbapi message") {
            val clipboard = McClient.clipboard.takeUnless { it.isEmpty() } ?: return@registerWithCallback
            val component = runCatching {
                JsonParser.parseString(clipboard).toData(ComponentSerialization.CODEC)
            }.getOrNull() ?: Text.of(clipboard)

            component.send()
            ChatReceivedEvent.Pre(component).post()
            ChatReceivedEvent.Post(component).post()
        }
    }
}
