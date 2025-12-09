package tech.thatgravyboat.skyblockapi.utils.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

internal class VirtualResourceArgument(
    private val locations: Collection<Identifier>,
    private val namespace: String = Identifier.DEFAULT_NAMESPACE,
) : ArgumentType<Identifier> {

    private val identifierNotFound: DynamicCommandExceptionType = DynamicCommandExceptionType { id: Any? ->
        Text.of("Identifier ") {
            append("$id") { this.color = TextColor.GOLD }
            append(" not found")
        }
    }

    override fun parse(reader: StringReader): Identifier {
        val resourceLocation: Identifier = this.fromCommandInput(reader)
        if (!locations.contains(resourceLocation)) {
            throw identifierNotFound.create(resourceLocation)
        }

        return resourceLocation
    }

    @Throws(CommandSyntaxException::class)
    private fun fromCommandInput(reader: StringReader): Identifier {
        val i = reader.cursor
        while (reader.canRead() && Identifiers.isAllowedInIdentifier(reader.peek())) {
            reader.skip()
        }
        val string = reader.string.substring(i, reader.cursor)
        val split: Array<String> = split(string)

        return Identifiers.parse(split[0], split[1]) ?: run {
            reader.cursor = i
            throw Identifier.ERROR_INVALID.createWithContext(reader)
        }
    }

    private fun split(id: String): Array<String> {
        val strings = arrayOf(this.namespace, id)
        val i = id.indexOf(':')
        if (i >= 0) {
            strings[1] = id.substring(i + 1)
            if (i >= 1) {
                strings[0] = id.substring(0, i)
            }
        }
        return strings
    }
}
