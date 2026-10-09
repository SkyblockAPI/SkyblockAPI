package tech.thatgravyboat.skyblockapi.api.events.misc

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.SharedSuggestionProvider
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.command.dsl.CommandBuilder0
import tech.thatgravyboat.skyblockapi.utils.command.dsl.CommandClass
import tech.thatgravyboat.skyblockapi.utils.command.dsl.command

public typealias LiteralCommandBuilder = CommandBuilder<LiteralArgumentBuilder<FabricClientCommandSource>>
public typealias ArgumentCommandBuilder<T> = CommandBuilder<RequiredArgumentBuilder<FabricClientCommandSource, T>>

@CommandClass
public data class BuilderDsl<Consumer>(val consumer: (Consumer) -> Unit) {
    public infix fun executes(callback: Consumer) {
        consumer(callback)
    }
}

public class RegisterCommandsEvent(private val dispatcher: CommandDispatcher<FabricClientCommandSource>, public val buildContext: CommandBuildContext?) : SkyBlockEvent() {

    @Deprecated("Also provide build context")
    public constructor(dispatcher: CommandDispatcher<FabricClientCommandSource>) : this(dispatcher, null)

    public fun register(command: LiteralArgumentBuilder<FabricClientCommandSource>) {
        dispatcher.register(command)
    }

    public fun command(name: String, init: CommandBuilder0<FabricClientCommandSource>.() -> Unit): Unit {
        dispatcher.command(name, buildContext!!, init)
    }

    public fun command(name: String): CommandBuilder0<FabricClientCommandSource> {
        return  dispatcher.command(name, buildContext!!) {}
    }

    public fun register(command: String, builder: LiteralCommandBuilder.() -> Unit) {
        if (command.contains(' ')) {
            val (literal, subcommand) = command.split(' ', limit = 2)
            register(literal) {
                then(subcommand, action = builder)
            }
            return
        }

        ClientCommands.literal(command)
            .apply { LiteralCommandBuilder(this).apply(builder) }
            .let(::register)
    }

    public fun registerWithCallback(command: String, callback: CommandContext<FabricClientCommandSource>.() -> Unit) {
        register(command) {
            this.callback(callback)
        }
    }

    public companion object {
        public inline fun <reified T> CommandContext<*>.argument(name: String): T = this.getArgument(name, T::class.java)
    }
}

public open class CommandBuilder<B : ArgumentBuilder<FabricClientCommandSource, B>>(
    public val builder: ArgumentBuilder<FabricClientCommandSource, B>,
) {

    public open fun callback(callback: CommandContext<FabricClientCommandSource>.() -> Unit) {
        this.builder.executes {
            callback(it)
            1
        }
    }

    public open fun then(vararg names: String, action: LiteralCommandBuilder.() -> Unit): CommandBuilder<B> {
        for (name in names) {
            if (name.contains(" ")) {
                val builder = CommandBuilder(ClientCommands.literal(name.substringBefore(" ")))
                builder.then(name.substringAfter(" "), action = action)
                this.builder.then(builder.builder)
                continue
            }
            val builder = CommandBuilder(ClientCommands.literal(name))
            builder.action()
            this.builder.then(builder.builder)
        }
        return this
    }

    public open fun <T> then(
        name: String,
        argument: ArgumentType<T>,
        suggestions: Collection<String>,
        action: ArgumentCommandBuilder<T>.() -> Unit,
    ): CommandBuilder<B> = then(
        name,
        argument,
        { _, builder -> SharedSuggestionProvider.suggest(suggestions, builder) },
        action,
    )

    public open fun <T> then(
        name: String,
        argument: ArgumentType<T>,
        suggestions: SuggestionProvider<FabricClientCommandSource>? = null,
        action: ArgumentCommandBuilder<T>.() -> Unit,
    ): CommandBuilder<B> {
        if (name.contains(" ")) {
            val builder = CommandBuilder(ClientCommands.literal(name.substringBefore(" ")))
            builder.then(name.substringAfter(" "), argument, suggestions, action)
            this.builder.then(builder.builder)
            return this
        }
        val builder = CommandBuilder(
            ClientCommands.argument(name, argument).apply {
                if (suggestions != null) suggests(suggestions)
            },
        )
        builder.action()
        this.builder.then(builder.builder)
        return this
    }

    public open fun thenCallback(vararg names: String, block: CommandContext<FabricClientCommandSource>.() -> Unit): CommandBuilder<B> {
        return then(*names) {
            this.callback(block)
        }
    }

    public open fun <T> thenCallback(
        name: String,
        argument: ArgumentType<T>,
        suggestions: Collection<String>,
        block: CommandContext<FabricClientCommandSource>.() -> Unit,
    ): CommandBuilder<B> = then(name, argument, suggestions) {
        this.callback(block)
    }


    public open fun <T> thenCallback(
        name: String,
        argument: ArgumentType<T>,
        suggestions: SuggestionProvider<FabricClientCommandSource>? = null,
        block: CommandContext<FabricClientCommandSource>.() -> Unit,
    ): CommandBuilder<B> = then(name, argument, suggestions) {
        this.callback(block)
    }

}
