package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

public object CommonText {

    public val NEWLINE: Component = "\n".asComponent()
    public val HYPHEN: Component = "-".asComponent()
    public val COMMA: Component = ",".asComponent()
    public val SPACE: Component = " ".asComponent()
    public val EMPTY: Component = "".asComponent()

    internal val PREFIX: Component = Text.of("[SkyBlockAPI]", TextColor.YELLOW)
}
