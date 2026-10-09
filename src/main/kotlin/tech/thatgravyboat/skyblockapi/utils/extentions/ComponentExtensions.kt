package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence

public fun FormattedCharSequence.asString(): String = buildString {
    accept { _, _, codepoint ->
        append(codepoint.toChar())
        true
    }
}

public fun FormattedCharSequence.styles(): Set<Style> = buildSet {
    accept { _, style, _ ->
        add(style)
        true
    }
}
