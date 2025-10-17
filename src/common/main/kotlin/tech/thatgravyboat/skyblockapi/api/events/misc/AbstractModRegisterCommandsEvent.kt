package tech.thatgravyboat.skyblockapi.api.events.misc

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

/** Utility event for creating commands with shared prefixes. */
abstract class AbstractModRegisterCommandsEvent(private val baseEvent: RegisterCommandsEvent, private vararg val prefixes: String) : SkyBlockEvent() {

    init {
        require(prefixes.isNotEmpty()) { "prefixes cannot be empty!" }
    }

    /** Default callback for when you execute the command with no args */
    fun registerBase(callback: CommandContext<FabricClientCommandSource>.() -> Unit) {
        prefixes.forEach { baseEvent.registerWithCallback(it, callback = callback) }
    }

    fun register(command: String, builder: LiteralCommandBuilder.() -> Unit) {
        prefixes.forEach { baseEvent.register("$it $command", builder = builder) }
    }

    fun registerWithCallback(command: String, callback: CommandContext<FabricClientCommandSource>.() -> Unit) {
        prefixes.forEach { baseEvent.registerWithCallback("$it $command", callback = callback) }
    }
}
