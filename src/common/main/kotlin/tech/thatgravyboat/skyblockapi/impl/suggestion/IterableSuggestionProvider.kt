package tech.thatgravyboat.skyblockapi.impl.suggestion

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.util.concurrent.CompletableFuture


data class IterableSuggestionProvider<T>(val providers: Iterable<T>, val transformer: (T) -> String = { it.toString() }) : SkyBlockAPISuggestionProvider {
    override fun getSuggestions(context: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> =
        CompletableFuture.supplyAsync {
            providers.forEach { suggest(builder, transformer(it)) }
            builder.build()
        }
}
