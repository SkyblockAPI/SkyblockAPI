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

class EnumArgument<E : Enum<E>> private constructor(
    clazz: Class<E>,
) : ArgumentType<E> {
    private val entries: Array<E> = clazz.enumConstants

    init {
        require(entries.map { it.name.lowercase() }.toSet().size == entries.size) {
            "Enum ${clazz.name} has multiple entries with the same name when lowercased."
        }
    }

    private val invalidValueException = DynamicCommandExceptionType { input: Any? ->
        Text.of("Enum entry ") {
            append("$input") { color = TextColor.GOLD }
            append(" not found")
        }
    }

    override fun parse(reader: StringReader): E {
        val input: String = reader.readString()
        return entries.find { it.name.equals(input, true) } ?: throw invalidValueException.createWithContext(reader, input)
    }

    override fun <S : Any?> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val remaining = builder.remaining.lowercase()
        entries.forEach { e ->
            if (e.name.startsWith(remaining, true)) builder.suggest(e.name)
        }
        return builder.buildFuture()
    }

    override fun getExamples(): Collection<String> = entries.map { it.name }

    companion object {
        fun <E : Enum<E>> create(clazz: Class<E>): EnumArgument<E> = EnumArgument(clazz)

        inline operator fun <reified E : Enum<E>> invoke(): EnumArgument<E> {
            return create(E::class.java)
        }
    }
}
