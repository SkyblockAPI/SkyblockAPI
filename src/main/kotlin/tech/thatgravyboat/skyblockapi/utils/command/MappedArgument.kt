package tech.thatgravyboat.skyblockapi.utils.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType

class MappedArgument<InputType, OutputType>(val base: ArgumentType<InputType>, val mapper: (InputType) -> OutputType) : ArgumentType<OutputType> {
    override fun parse(reader: StringReader): OutputType {
        return mapper(base.parse(reader))
    }
}

fun <InputType, OutputType> ArgumentType<InputType>.mapped(mapper: (InputType) -> OutputType): ArgumentType<OutputType> = MappedArgument(this, mapper)
