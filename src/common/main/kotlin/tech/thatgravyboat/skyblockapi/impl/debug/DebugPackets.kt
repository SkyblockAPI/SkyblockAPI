package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.gson.JsonElement
import com.mojang.datafixers.util.Either
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketType
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketSentEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.debug.packets.DebugWriter.toJson
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import kotlin.time.Clock
import kotlin.time.Instant

@Module
object DebugPackets {

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
            McClient.setScreen(DebugPacketsScreen(entries))
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
}

@Stub
internal expect fun DebugPacketsScreen(messages: List<Pair<Instant, DebugPackets.PacketEntry>>): Screen
