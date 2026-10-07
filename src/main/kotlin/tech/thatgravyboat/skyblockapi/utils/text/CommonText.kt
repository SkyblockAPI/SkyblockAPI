package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

object CommonText {

    val NEWLINE: Component = "\n".asComponent()
    val HYPHEN: Component = "-".asComponent()
    val COMMA: Component = ",".asComponent()
    val SPACE: Component = " ".asComponent()
    val EMPTY: Component = "".asComponent()

    internal val PREFIX: Component = Text.of("[SkyBlockAPI]", TextColor.YELLOW)
}
