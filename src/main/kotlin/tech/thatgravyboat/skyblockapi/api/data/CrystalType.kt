package tech.thatgravyboat.skyblockapi.api.data

import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

enum class CrystalType(val textColor: Int, skyblockId: String? = null) {
    JADE(TextColor.GREEN),
    AMBER(TextColor.GOLD),
    AMETHYST(TextColor.DARK_PURPLE),
    SAPPHIRE(TextColor.AQUA),
    TOPAZ(TextColor.YELLOW),
    JASPER(TextColor.LIGHT_PURPLE),
    RUBY(TextColor.RED),
    OPAL(TextColor.WHITE),
    AQUAMARINE(TextColor.DARK_BLUE),
    PERIDOT(TextColor.DARK_GREEN),
    ONYX(TextColor.DARK_GRAY),
    CITRINE(TextColor.DARK_RED),
    ;

    val id = SkyBlockId.item(skyblockId ?: "${name.lowercase()}_crystal")

    val displayName = Text.of("${toFormattedName()} Crystal") {
        this.color = textColor
    }
}

enum class CrystalStatus {
    NOT_FOUND,
    FOUND,
    PLACED,
}
