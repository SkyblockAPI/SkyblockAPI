package tech.thatgravyboat.skyblockapi.api.events.misc

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

typealias LiteralCommandBuilder = CommandBuilder<LiteralArgumentBuilder<FabricClientCommandSource>>
typealias ArgumentCommandBuilder<T> = CommandBuilder<RequiredArgumentBuilder<FabricClientCommandSource, T>>


class RegisterCommandsEvent(private val dispatcher: CommandDispatcher<FabricClientCommandSource>) : SkyblockEvent() {

    fun register(command: LiteralArgumentBuilder<FabricClientCommandSource>) {
        dispatcher.register(command)
    }

    fun register(command: String, builder: LiteralCommandBuilder.() -> Unit) {
        ClientCommandManager.literal(command)
            ?.apply { LiteralCommandBuilder(this).apply(builder) }
            ?.let(dispatcher::register)
    }
}

class CommandBuilder<B : ArgumentBuilder<FabricClientCommandSource, B>> internal constructor(
    private val builder: ArgumentBuilder<FabricClientCommandSource, B>,
) {

    fun callback(callback: CommandContext<FabricClientCommandSource>.() -> Unit) {
        this.builder.executes {
            callback(it)
            1
        }
    }

    fun then(name: String, action: LiteralCommandBuilder.() -> Unit): CommandBuilder<B> {
        val builder = CommandBuilder(ClientCommandManager.literal(name))
        builder.action()
        this.builder.then(builder.builder)
        return this
    }

    fun <T> then(
        name: String,
        argument: ArgumentType<T>,
        suggestions: List<String>,
        action: ArgumentCommandBuilder<T>.() -> Unit,
    ): CommandBuilder<B> = then(
        name,
        argument,
        { _, builder -> SharedSuggestionProvider.suggest(suggestions, builder) },
        action,
    )

    fun <T> then(
        name: String,
        argument: ArgumentType<T>,
        suggestions: SuggestionProvider<FabricClientCommandSource>? = null,
        action: ArgumentCommandBuilder<T>.() -> Unit,
    ): CommandBuilder<B> {
        val builder = CommandBuilder(
            ClientCommandManager.argument(name, argument).apply {
                if (suggestions != null) suggests(suggestions)
            },
        )
        builder.action()
        this.builder.then(builder.builder)
        return this
    }

    internal fun build(): ArgumentBuilder<FabricClientCommandSource, B> = builder
}
