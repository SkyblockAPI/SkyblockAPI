package tech.thatgravyboat.skyblockapi.utils.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType

public class MappedArgument<InputType, OutputType>(public val base: ArgumentType<InputType>, public val mapper: (InputType) -> OutputType) : ArgumentType<OutputType> {
    override fun parse(reader: StringReader): OutputType {
        return mapper(base.parse(reader))
    }
}

public fun <InputType, OutputType> ArgumentType<InputType>.mapped(mapper: (InputType) -> OutputType): ArgumentType<OutputType> = MappedArgument(this, mapper)
