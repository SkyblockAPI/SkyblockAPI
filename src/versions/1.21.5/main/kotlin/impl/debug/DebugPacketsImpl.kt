@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.impl.debug

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.protocol.PacketFlow
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.toComponent
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Instant

private val packetToastId = SystemToast.SystemToastId(1500)
private fun copyToClipboard(text: String, message: String) {
    McClient.clipboard = text
    SystemToast.add(
        McClient.toasts,
        packetToastId,
        Text.of("[SkyBlock API]") { this.color = TextColor.YELLOW },
        Text.of(message) { this.color = TextColor.YELLOW },
    )
}
internal actual fun DebugPacketsScreen(messages: List<Pair<Instant, DebugPackets.PacketEntry>>): Screen {
    fun export() {
        val header = "timestamp,direction,type,packet"
        val content = messages.mapNotNull { (timestamp, entry) ->
            val packet = entry.content.left().getOrNull()?.toString() ?: return@mapNotNull null
            val direction = when (entry.type.flow()) {
                PacketFlow.CLIENTBOUND -> "S->C"
                PacketFlow.SERVERBOUND -> "C->S"
            }
            val type = entry.type.id.toShortLanguageKey()
            "${timestamp.epochSeconds},$direction,$type,$packet"
        }
        copyToClipboard("$header\n${content.joinToString("\n")}", "Export copied to clipboard!")
    }

    return DebugScreenImpl(
        title = "Packets",
        messages = messages,
        buttons = listOf(
            Button.builder(Text.of("Export")) { export() }
                .size(100, 16)
                .build()
        ),
        timeFormat = "HH:mm:ss.SSS",
        asSearch = { "${it.type.id.toShortLanguageKey()} ${it.content.map(Any::toString, Any::toString)}" },
        display = { packet ->
            Text.join(
                when (packet.type.flow()) {
                    PacketFlow.CLIENTBOUND -> Text.of("S -> C") { this.color = TextColor.BLUE }
                    PacketFlow.SERVERBOUND -> Text.of("C -> S") { this.color = TextColor.LIGHT_PURPLE }
                },
                " ",
                Text.of(packet.type.id.toShortLanguageKey()) { this.color = TextColor.YELLOW },
                " ",
                packet.content.map({ it.toComponent(1, false) }, { Text.of("<error serializing>(${it})") { this.color = TextColor.RED } }),
            )
        },
        tooltip = { packet ->
            packet.content.map(
                {
                    Text.multiline(
                        it.toComponent(4, true),
                        "",
                        Text.of("Click to copy to clipboard") { this.color = TextColor.GRAY },
                    )
                },
                { Text.of(it.stackTraceToString()) { this.color = TextColor.RED } },
            )
        },
        onClicked = { packet ->
            packet.content
                .ifLeft { copyToClipboard(it.toPrettyString(), "Packet copied to clipboard!") }
                .ifRight { copyToClipboard(it.stackTraceToString(), "Error message copied to clipboard!") }
        },
    )
}
