package tech.thatgravyboat.skyblockapi.utils.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.sanitizeForCommandInput
import java.util.concurrent.CompletableFuture

public data class SkyBlockIdArgument(val skyblockIds: Iterable<SkyBlockId> = SimpleItemAPI.getAllIds(), val filter: (SkyBlockId) -> Boolean = { true }) :
    ArgumentType<SkyBlockId>, SuggestionProvider<FabricClientCommandSource> {

    override fun parse(reader: StringReader): SkyBlockId {
        val cursor = reader.cursor
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip()
        }
        val string = reader.string.substring(cursor, reader.cursor)


        return skyblockIds.find { it.id == string.lowercase() } ?: SkyBlockId.unsafe(string.lowercase())
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        skyblockIds.forEach {
            suggest(builder, it.id.lowercase())
        }
        return builder.buildFuture()
    }

    private fun suggest(builder: SuggestionsBuilder, name: String) {
        val filtered = name.sanitizeForCommandInput()
        if (SharedSuggestionProvider.matchesSubStr(builder.remaining.lowercase(), filtered.lowercase())) {
            builder.suggest(filtered)
        }
    }

    override fun getSuggestions(
        p0: CommandContext<FabricClientCommandSource>,
        p1: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> = listSuggestions(p0, p1)
}
