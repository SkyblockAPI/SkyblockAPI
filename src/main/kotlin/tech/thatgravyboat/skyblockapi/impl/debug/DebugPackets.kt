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
import tech.thatgravyboat.skyblockapi.api.events.level.PacketEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.debug.packets.DebugWriter.toJson
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.json.Json.toComponent
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.suggest
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Instant

@Module
public object DebugPackets {

    private data class StoredPacket(
        val packet: Packet<*>,
        val cancelled: Boolean,
        val instant: Instant = currentInstant(),
    )

    private val packetToastId = SystemToast.SystemToastId(1500)
    private var logPackets = false
    private val packets = mutableListOf<StoredPacket>()
    private var entries = listOf<Pair<Instant, PacketEntry>>()

    @Subscription
    internal fun onCommandRegistration(event: RegisterSkyblockApiCommandsEvent) {
        event.register("logpackets") {
            callback {
                logPackets = !logPackets
                Text.sendDebug("Packet logging is now ${if (logPackets) "enabled" else "disabled"}")

                // TODO: maybe make this part async?
                if (!logPackets && packets.isNotEmpty()) {
                    entries = packets.map { (packet, cancelled, time) ->
                        runCatching {
                            time to PacketEntry(packet.type(), Either.left(packet.toJson()), cancelled)
                        }.getOrElse { error ->
                            time to PacketEntry(packet.type(), Either.right(error), cancelled)
                        }
                    }
                    packets.clear()
                    Text.sendDebug("You have logged ${entries.size} packets. Use /sbapi logpackets open to view them.") {
                        suggest = "/sbapi logpackets open"
                    }
                }
            }
            thenCallback("open") {
                if (entries.isEmpty()) {
                    Text.sendDebug("No packets have been logged yet.")
                    return@thenCallback
                }

                McClient.setScreen(createScreen(entries))
            }
            thenCallback("export") {
                if (entries.isEmpty()) {
                    Text.sendDebug("No packets have been logged yet.")
                    return@thenCallback
                }

                export(entries)
            }
        }
    }

    // We set the priority as the lowest possible, that way we can know for sure what packets are being cancelled
    // Also, since both packet received and packet sent events extend PacketEvent, we can simply just use this one
    @Subscription(priority = Int.MAX_VALUE, receiveCancelled = true)
    public fun onPacket(event: PacketEvent) {
        if (!logPackets) return
        this.packets.add(StoredPacket(event.packet, event.isCancelled))
    }

    public data class PacketEntry(
        val type: PacketType<*>,
        val content: Either<JsonElement, Throwable>,
        val cancelled: Boolean,
    )

    private fun copyToClipboard(text: String, message: String) {
        McClient.clipboard = text
        SystemToast.add(
            McClient.toasts,
            packetToastId,
            CommonText.PREFIX,
            Text.of(message, TextColor.YELLOW),
        )
    }

    private fun export(messages: List<Pair<Instant, PacketEntry>>) {
        val header = "timestamp,direction,type,cancelled,packet"
        val content = messages.mapNotNull { (timestamp, entry) ->
            val packet = entry.content.left().getOrNull()?.toString() ?: return@mapNotNull null
            val direction = when (entry.type.flow()) {
                PacketFlow.CLIENTBOUND -> "S->C"
                PacketFlow.SERVERBOUND -> "C->S"
            }
            val type = entry.type.id().toShortLanguageKey()
            val cancelled = if (entry.cancelled) "Y" else "N"
            "${timestamp.epochSeconds},$direction,$type,$cancelled,$packet"
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
                        PacketFlow.CLIENTBOUND -> Text.of("S -> C", TextColor.BLUE)
                        PacketFlow.SERVERBOUND -> Text.of("C -> S", TextColor.LIGHT_PURPLE)
                    },
                    if (packet.cancelled) Text.of("❌", TextColor.RED) else null,
                    Text.of(packet.type.id().toShortLanguageKey(), TextColor.YELLOW),
                    packet.content.map({ it.toComponent(1, false) }, { Text.of("<error serializing>(${it})", TextColor.RED) }),
                    separator = CommonText.SPACE,
                )
            },
            tooltip = { packet ->
                packet.content.map(
                    {
                        Text.join(
                            it.toComponent(4, true),
                            if (packet.cancelled) Text.of("This packet was cancelled!", TextColor.RED) else null,
                            Text.of("Click to copy to clipboard") { this.color = TextColor.GRAY },
                            separator = Text.of("\n\n"),
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
