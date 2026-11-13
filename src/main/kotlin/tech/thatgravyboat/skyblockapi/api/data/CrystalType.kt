package tech.thatgravyboat.skyblockapi.api.data

import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

public enum class CrystalType(public val textColor: Int, public val placeable: Boolean = false, skyblockId: String? = null) {
    JADE(TextColor.GREEN, true),
    AMBER(TextColor.GOLD, true),
    AMETHYST(TextColor.DARK_PURPLE, true),
    SAPPHIRE(TextColor.AQUA, true),
    TOPAZ(TextColor.YELLOW, true),
    JASPER(TextColor.LIGHT_PURPLE),
    RUBY(TextColor.RED),
    OPAL(TextColor.WHITE),
    AQUAMARINE(TextColor.DARK_BLUE),
    PERIDOT(TextColor.DARK_GREEN),
    ONYX(TextColor.DARK_GRAY),
    CITRINE(TextColor.DARK_RED),
    ;

    public val id: SkyBlockId = SkyBlockId.item(skyblockId ?: "${name.lowercase()}_crystal")

    public val displayName: MutableComponent = Text.of("${toFormattedName()} Crystal") {
        this.color = textColor
    }

    public companion object {
        public fun byName(name: String): CrystalType? {
            return entries.find { it.name.equals(name, true) || "${it.name} Crystal".equals(name, true) }
        }
    }
}

public enum class CrystalStatus {
    NOT_FOUND,
    FOUND,
    PLACED,
    ;

    public companion object {
        public fun fromString(str: String): CrystalStatus? = when (str) {
            "Not Found", "✖ Not Found" -> NOT_FOUND
            "Not Placed", "✖ Not Placed", "Found", "✔ Found" -> FOUND
            "Placed", "✔ Placed" -> PLACED
            else -> null
        }
    }
}
