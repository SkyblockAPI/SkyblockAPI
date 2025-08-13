package tech.thatgravyboat.skyblockapi.utils.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.ResourceLocationException
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

class VirtualResourceArgument(
    private val locations: Collection<ResourceLocation>,
    private val namespace: String = ResourceLocation.DEFAULT_NAMESPACE,
) : ArgumentType<ResourceLocation> {

    private val commandException: SimpleCommandExceptionType = SimpleCommandExceptionType(Component.translatable("argument.id.invalid"))
    private val identifierNotFound: DynamicCommandExceptionType = DynamicCommandExceptionType { id: Any? ->
        Text.of("Identifier ") {
            append("$id") { this.color = TextColor.GOLD }
            append(" not found")
        }
    }

    override fun parse(reader: StringReader): ResourceLocation {
        val resourceLocation: ResourceLocation = this.fromCommandInput(reader)
        if (!locations.contains(resourceLocation)) {
            throw identifierNotFound.create(resourceLocation)
        }

        return resourceLocation
    }

    @Throws(CommandSyntaxException::class)
    private fun fromCommandInput(reader: StringReader): ResourceLocation {
        val i = reader.cursor
        while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
            reader.skip()
        }
        val string = reader.string.substring(i, reader.cursor)
        try {
            val split: Array<String> = split(string)
            return ResourceLocation.fromNamespaceAndPath(split[0], split[1])
        } catch (_: ResourceLocationException) {
            reader.cursor = i
            throw commandException.createWithContext(reader)
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
