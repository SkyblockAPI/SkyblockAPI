@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.impl.debug

import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import kotlin.time.Instant

private val chatToastId = SystemToast.SystemToastId(1500)
internal actual fun DebugChatScreen(messages: List<Pair<Instant, Component>>): Screen = DebugScreenImpl(
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
