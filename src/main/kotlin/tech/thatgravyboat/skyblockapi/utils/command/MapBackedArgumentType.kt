package tech.thatgravyboat.skyblockapi.utils.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.concurrent.CompletableFuture

class MapBackedArgumentType<T>(
    private val map: Map<String, T>,
    private val ignoreCase: Boolean = true,
) : ArgumentType<T> {

    private val elementNotFound: DynamicCommandExceptionType = DynamicCommandExceptionType { id: Any? ->
        Text.of("Element '") {
            append("$id") { this.color = TextColor.GOLD }
            append("' not found")
        }
    }

    override fun parse(reader: StringReader): T {
        val input = reader.readUnquotedString()

        val value = if (!ignoreCase) map[input]
        else map.entries.find { it.key.equals(input, true) }?.value

        return value ?: throw elementNotFound.create(input)
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val input = builder.remaining
        map.keys.forEach { id ->
            if (id.startsWith(input, ignoreCase)) builder.suggest(id)
        }
        return builder.buildFuture()
    }
}

