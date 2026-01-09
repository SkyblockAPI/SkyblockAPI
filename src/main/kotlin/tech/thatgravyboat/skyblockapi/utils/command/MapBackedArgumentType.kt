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

class MapBackedArgumentType<KeyType, ValueType>(
    private val map: Map<KeyType, ValueType>,
    private val ignoreCase: Boolean = true,
    private val keyTransformer: (KeyType) -> String = { it.toString() },
) : ArgumentType<ValueType> {

    private val elementNotFound: DynamicCommandExceptionType = DynamicCommandExceptionType { id: Any? ->
        Text.of("Element '") {
            append("$id") { this.color = TextColor.GOLD }
            append("' not found")
        }
    }

    override fun parse(reader: StringReader): ValueType {
        val input = reader.readUnquotedString()

        val value = if (!ignoreCase) map.entries.find { (key) -> keyTransformer(key) == input }?.value
        else map.entries.find { keyTransformer(it.key).equals(input, true) }?.value

        return value ?: throw elementNotFound.create(input)
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val input = builder.remaining
        map.keys.forEach { id ->
            val key = keyTransformer(id)
            if (key.startsWith(input, ignoreCase)) builder.suggest(key)
        }
        return builder.buildFuture()
    }
}

