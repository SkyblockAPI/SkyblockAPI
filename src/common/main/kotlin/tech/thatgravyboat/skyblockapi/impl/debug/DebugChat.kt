package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.gson.JsonParser
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import kotlin.time.Clock
import kotlin.time.Instant

@Module
object DebugChat {

    private val messages = mutableListOf<Pair<Instant, Component>>()

    @Subscription(priority = Int.MIN_VALUE, receiveCancelled = true)
    fun onMessage(event: ChatReceivedEvent.Pre) {
        messages.add(Clock.System.now() to event.component)
    }

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.registerWithCallback("sbapi chat") {
            McClient.setScreen(DebugChatScreen(messages))
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

@Stub
internal expect class DebugChatScreen(messages: List<Pair<Instant, Component>>) : Screen
