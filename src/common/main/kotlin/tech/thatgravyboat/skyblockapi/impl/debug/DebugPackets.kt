package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.gson.JsonElement
import com.mojang.datafixers.util.Either
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.PacketType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketSentEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.debug.packets.DebugWriter.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toComponent
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Clock
import kotlin.time.Instant

@Module
object DebugPackets {

    private val packetToastId = SystemToast.SystemToastId(1500)
    private var logPackets = false
    private val packets = mutableListOf<Pair<Instant, Packet<*>>>()
    private var entries = listOf<Pair<Instant, PacketEntry>>()

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        event.registerWithCallback("sbapi logpackets") {
            logPackets = !logPackets
            Text.debug("Packet logging is now ${if (logPackets) "enabled" else "disabled"}").send()

            if (!logPackets && packets.isNotEmpty()) {
                entries = packets.map { (time, packet) ->
                    runCatching {
                        time to PacketEntry(packet.type(), Either.left(packet.toJson()))
                    }.getOrElse { error ->
                        time to PacketEntry(packet.type(), Either.right(error))
                    }
                }
                packets.clear()
                Text.debug("You have logged ${entries.size} packets. Use /sbapi logpackets open to view them.").send()
            }
        }

        event.registerWithCallback("sbapi logpackets open") {
            if (entries.isEmpty()) {
                Text.debug("No packets have been logged yet.").send()
                return@registerWithCallback
            }

            McClient.setScreen(createScreen(entries))
        }

        event.registerWithCallback("sbapi logpackets export") {
            if (entries.isEmpty()) {
                Text.debug("No packets have been logged yet.").send()
                return@registerWithCallback
            }

            export(entries)
        }
    }

    @Subscription
    fun onPacketReceived(event: PacketReceivedEvent) {
        if (!logPackets) return
        this.packets.add(Clock.System.now() to event.packet)
    }

    @Subscription
    fun onPacketSent(event: PacketSentEvent) {
        if (!logPackets) return
        this.packets.add(Clock.System.now() to event.packet)
    }

    data class PacketEntry(
        val type: PacketType<*>,
        val content: Either<JsonElement, Throwable>
    )

    private fun copyToClipboard(text: String, message: String) {
        McClient.clipboard = text
        SystemToast.add(
            McClient.toasts,
            packetToastId,
            Text.of("[SkyBlock API]") { this.color = TextColor.YELLOW },
            Text.of(message) { this.color = TextColor.YELLOW },
        )
    }

    private fun export(messages: List<Pair<Instant, PacketEntry>>) {
        val header = "timestamp,direction,type,packet"
        val content = messages.mapNotNull { (timestamp, entry) ->
            val packet = entry.content.left().getOrNull()?.toString() ?: return@mapNotNull null
            val direction = when (entry.type.flow()) {
                PacketFlow.CLIENTBOUND -> "S->C"
                PacketFlow.SERVERBOUND -> "C->S"
            }
            val type = entry.type.id().toShortLanguageKey()
            "${timestamp.epochSeconds},$direction,$type,$packet"
        }
        copyToClipboard("$header\n${content.joinToString("\n")}", "Export copied to clipboard!")
    }

    private fun createScreen(messages: List<Pair<Instant, PacketEntry>>): Screen {
        return DebugScreen(
            title = "Packets",
            messages = messages,
            buttons = listOf(
                Button.builder(Text.of("Export")) { export(messages) }
                    .size(100, 16)
                    .build()
            ),
            timeFormat = "HH:mm:ss.SSS",
            asSearch = { "${it.type.id().toShortLanguageKey()} ${it.content.map(Any::toString, Any::toString)}" },
            display = { packet ->
                Text.join(
                    when (packet.type.flow()) {
                        PacketFlow.CLIENTBOUND -> Text.of("S -> C") { this.color = TextColor.BLUE }
                        PacketFlow.SERVERBOUND -> Text.of("C -> S") { this.color = TextColor.LIGHT_PURPLE }
                    },
                    " ",
                    Text.of(packet.type.id().toShortLanguageKey()) { this.color = TextColor.YELLOW },
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
}
