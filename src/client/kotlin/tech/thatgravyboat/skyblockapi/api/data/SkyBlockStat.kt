package tech.thatgravyboat.skyblockapi.api.data

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Suppress("unused")
enum class SkyBlockStat(
    val icon: Char,
    val color: Int,
    name: String? = null,
) {
    HEALTH('❤', TextColor.RED),
    DEFENSE('❈', TextColor.GREEN),
    STRENGTH('❁', TextColor.RED),
    INTELLIGENCE('✎', TextColor.AQUA),
    SPEED('✦', TextColor.WHITE),
    CRIT_DAMAGE('☠', TextColor.BLUE),
    CRIT_CHANCE('☣', TextColor.BLUE),
    ATTACK_SPEED('⚔', TextColor.YELLOW),
    // TODO: add more stats,
    ;

    private val displayName: String = name ?: toFormattedName()

    override fun toString(): String = displayName

    val displayText: Component = Text.of("$icon $displayName") {
        this@of.color = this@SkyBlockStat.color
    }

    companion object {
        fun fromName(name: String): SkyBlockStat? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
