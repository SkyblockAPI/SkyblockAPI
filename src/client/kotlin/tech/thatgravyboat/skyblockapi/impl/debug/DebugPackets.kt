package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.network.protocol.PacketType
import net.minecraft.network.protocol.game.GameProtocols
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketSentEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugPackets {

    private var logPackets = false
    private val toLog: MutableSet<PacketType<*>> = mutableSetOf()
    private val summary: MutableMap<PacketType<*>, Int> = mutableMapOf()

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        val packetTypes = mutableMapOf<ResourceLocation, PacketType<*>>()
        GameProtocols.CLIENTBOUND_TEMPLATE.listPackets { packetType, _ ->
            packetTypes[packetType.id.withPrefix("clientbound_")] = packetType
        }
        GameProtocols.SERVERBOUND_TEMPLATE.listPackets { packetType, _ ->
            packetTypes[packetType.id.withPrefix("serverbound_")] = packetType
        }
        val packetKeys = packetTypes.keys.map(ResourceLocation::toString).toList()

        event.register("sbapi") {
            then("logpackets") {
                callback {
                    logPackets = !logPackets
                    Text.of("[SkyBlockAPI] Packet logging is now ${if (logPackets) "enabled" else "disabled"}") {
                        this.color = TextColor.YELLOW
                    }.send()

                    if (!logPackets) {
                        Text.of("[SkyBlockAPI] Packet summary:") {
                            this.color = TextColor.YELLOW
                        }.send()
                        summary.entries.sortedBy { it.value }.forEach { (packet, count) ->
                            Text.of("${packet.id} - $count") {
                                this.color = TextColor.YELLOW
                            }.send()
                        }
                        summary.clear()
                    }
                }

                then("list") {
                    callback {
                        Text.of("[SkyBlockAPI] Packets to log:") {
                            this.color = TextColor.YELLOW
                        }.send()
                        toLog.forEach {
                            Text.of(it.id.toString()) {
                                this.color = TextColor.YELLOW
                            }.send()
                        }
                    }
                }

                then("add") {
                    then("packet", StringArgumentType.greedyString(), packetKeys) {
                        callback {
                            val id = ResourceLocation.tryParse(StringArgumentType.getString(this, "packet")) ?: return@callback
                            val packetType = packetTypes[id] ?: return@callback
                            toLog.add(packetType)
                            Text.of("[SkyBlockAPI] Added packet ${packetType.id} to log list") {
                                this.color = TextColor.YELLOW
                            }.send()
                        }
                    }
                }

                then("remove") {
                    then("packet", StringArgumentType.greedyString(), packetKeys) {
                        callback {
                            val id = ResourceLocation.tryParse(StringArgumentType.getString(this, "packet")) ?: return@callback
                            val packetType = packetTypes[id] ?: return@callback
                            toLog.remove(packetType)
                            Text.of("[SkyBlockAPI] Removed packet ${packetType.id} to log list") {
                                this.color = TextColor.YELLOW
                            }.send()
                        }
                    }
                }
            }
        }
    }

    @Subscription
    fun onPacketReceived(event: PacketReceivedEvent) {
        if (!logPackets) return
        val packet = event.packet
        if (toLog.isNotEmpty() && packet.type() !in toLog) return
        SkyBlockAPI.logger.info("[Packet] In: ${packet.type().id} - $packet")
        this.summary[packet.type()] = (this.summary[packet.type()] ?: 0) + 1
    }

    @Subscription
    fun onPacketSent(event: PacketSentEvent) {
        if (!logPackets) return
        val packet = event.packet
        if (toLog.isNotEmpty() && packet.type() !in toLog) return
        SkyBlockAPI.logger.info("[Packet] Out: ${packet.type().id} - $packet")
        this.summary[packet.type()] = (this.summary[packet.type()] ?: 0) + 1
    }
}
