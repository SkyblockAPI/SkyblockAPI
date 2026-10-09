package tech.thatgravyboat.skyblockapi.api.events.misc

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.command.dsl.CommandBuilder0

/** Utility event for creating commands with shared prefixes. */
public abstract class AbstractModRegisterCommandsEvent(
    private val baseEvent: RegisterCommandsEvent,
    prefix: String,
    vararg extraPrefixes: String = emptyArray(),
) : SkyBlockEvent() {

    private val prefixes = listOf(prefix, *extraPrefixes)

    /** Default callback for when you execute the command with no args */
    public open fun registerBaseCallback(callback: CommandContext<FabricClientCommandSource>.() -> Unit) {
        prefixes.forEach { baseEvent.registerWithCallback(it, callback = callback) }
    }

    public open fun register(command: String, builder: LiteralCommandBuilder.() -> Unit) {
        prefixes.forEach { baseEvent.register("$it $command", builder = builder) }
    }

    public open fun registerWithCallback(command: String, callback: CommandContext<FabricClientCommandSource>.() -> Unit) {
        prefixes.forEach { baseEvent.registerWithCallback("$it $command", callback = callback) }
    }

    public open fun command(name: String, init: CommandBuilder0<FabricClientCommandSource>.() -> Unit) = prefixes.forEach {
        baseEvent.command("$it $name", init)
    }

    public open fun command(name: String): BuilderDsl<() -> Unit> {
        val builders = prefixes.map {
            baseEvent.command("$it $name")
        }

        return BuilderDsl {
            builders.forEach { dsl -> dsl.executes(it) }
        }
    }
}

internal class RegisterSkyblockApiCommandsEvent(
    baseEvent: RegisterCommandsEvent
) : AbstractModRegisterCommandsEvent(baseEvent, "sbapi", "skyblockapi")
