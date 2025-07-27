package tech.thatgravyboat.skyblockapi.api.remote.api

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

enum class CurrencyType(val displayName: Component) {
    COIN(Text.of("Coin") { this.color = TextColor.GOLD }),
    BIT(Text.of("Bit") { this.color = TextColor.AQUA }),
    COPPER(Text.of("Copper") { this.color = TextColor.RED }),
    FOSSIL_DUST(Text.of("Fossil Dust") { this.color = TextColor.WHITE }),
    BRONZE_MEDAL(Text.of("Bronze Medal") { this.color = TextColor.RED }),
    SILVER_MEDAL(Text.of("Silver Medal") { this.color = TextColor.WHITE }),
    GOLD_MEDAL(Text.of("Gold Medal") { this.color = TextColor.GOLD }),
    MOTE(Text.of("Motes") { this.color = TextColor.PINK }),
    NORTH_STAR(Text.of("North Stars") { this.color = TextColor.PINK }),
    PELT(Text.of("Pelt") { this.color = TextColor.MAGENTA }),
    GEM(Text.of("Gems") { this.color = TextColor.GREEN }),
    CHOCOLATE(Text.of("Chocolate") { this.color = TextColor.GOLD }),
    ;
}
