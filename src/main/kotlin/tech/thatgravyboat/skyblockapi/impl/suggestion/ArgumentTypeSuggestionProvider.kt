package tech.thatgravyboat.skyblockapi.impl.suggestion

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

data class ArgumentTypeSuggestionProvider<Type>(val argumentType: ArgumentType<*>) : SuggestionProvider<Type> {
    override fun getSuggestions(
        context: CommandContext<Type>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        return argumentType.listSuggestions(context, builder)
    }
}
