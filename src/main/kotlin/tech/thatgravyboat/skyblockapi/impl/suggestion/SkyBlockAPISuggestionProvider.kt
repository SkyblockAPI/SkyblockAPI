package tech.thatgravyboat.skyblockapi.impl.suggestion

import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import tech.thatgravyboat.skyblockapi.utils.extentions.sanitizeForCommandInput

public interface SkyBlockAPISuggestionProvider : SuggestionProvider<FabricClientCommandSource> {

    public fun suggest(builder: SuggestionsBuilder, name: String) {
        val filtered = name.sanitizeForCommandInput()
        if (SharedSuggestionProvider.matchesSubStr(builder.remaining.lowercase(), filtered.lowercase())) {
            builder.suggest(filtered)
        }
    }

}

public abstract class SkyBlockAPICommandSuggestionProvider : SuggestionProvider<FabricClientCommandSource> {

    private var sanitizeInput = true

    protected fun suggest(builder: SuggestionsBuilder, name: String) {
        val filtered = if (sanitizeInput) name.sanitizeForCommandInput() else name
        if (SharedSuggestionProvider.matchesSubStr(builder.remaining.lowercase(), filtered.lowercase())) {
            builder.suggest(filtered)
        }
    }

    public fun withoutSanitization() : SkyBlockAPICommandSuggestionProvider {
        this.sanitizeInput = false
        return this
    }

}
