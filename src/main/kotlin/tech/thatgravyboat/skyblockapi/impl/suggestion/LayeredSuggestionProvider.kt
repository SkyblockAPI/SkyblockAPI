package tech.thatgravyboat.skyblockapi.impl.suggestion

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.util.concurrent.CompletableFuture

data class LayeredSuggestionProvider(val providers: Iterable<SuggestionProvider<FabricClientCommandSource>>) : SuggestionProvider<FabricClientCommandSource> {
    constructor(vararg providers: SuggestionProvider<FabricClientCommandSource>) : this(providers.toList())

    override fun getSuggestions(
        context: CommandContext<FabricClientCommandSource>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        providers.forEach {
            it.getSuggestions(context, builder)
        }

        return builder.buildFuture()
    }
}
