package tech.thatgravyboat.skyblockapi.api.environmental

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.mining.GlaciteAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

enum class WeatherIntensity {
    MILD,
    EXTREME
}

enum class WeatherType(val icon: Char, val color: Int, weatherName: String? = null) {
    RAIN('\uE09A', TextColor.AQUA),
    THUNDERSTORM('\uE000', TextColor.YELLOW),
    SMOG('\uE09D', TextColor.GRAY),
    ACID_RAIN('\uE090', TextColor.GREEN),
    ASHFALL('\uE091', TextColor.DARK_GRAY),
    HELLSTORM('\uE097', TextColor.RED),
    MOONFALL('\uE099', TextColor.BLUE),
    TROPICAL_RAIN('\uE101', TextColor.AQUA),
    BLOSSOMING('\uE093', TextColor.LIGHT_PURPLE),
    BLOOMING('\uE09B', TextColor.LIGHT_PURPLE),
    BREEZE('\uE094', TextColor.AQUA),
    MIST('\uE098', TextColor.GRAY),
    ROCKFALL('\uE09C', TextColor.GRAY),
    SNOWSTORM('\uE09E', TextColor.WHITE),
    WISPFALL('\uE096', TextColor.GRAY),
    VOIDSTORM('\uE095', TextColor.DARK_PURPLE),
    BLIZZARD('\uE092', TextColor.WHITE),
    ;

    val weatherName = weatherName ?: toFormattedName()
    val component = Text.of("$icon ${this.weatherName}", color)
    val iconComponent = Text.of(icon.toString(), color)
}

data class SkyBlockWeather(
    val type: WeatherType,
    val intensity: WeatherIntensity,
    val bonuses: Map<SkyBlockStat, Double>,
    val specialEffect: Component? = null,
)

enum class SkyBlockWeatherGroup(
    val island: SkyBlockIsland,
    val mild: SkyBlockWeather,
    val extreme: SkyBlockWeather,
) {
    DWARVEN_MINES(
        island = SkyBlockIsland.DWARVEN_MINES,
        mild = SkyBlockWeather(
            type = BREEZE,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 100.0,
                SkyBlockStat.MINING_FORTUNE to 25.0,
                SkyBlockStat.FISHING_SPEED to 25.0,
            ),
        ),
        extreme = SkyBlockWeather(
            type = MIST,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 250.0,
                SkyBlockStat.MINING_FORTUNE to 50.0,
                SkyBlockStat.FISHING_SPEED to 50.0,
            ),
            specialEffect = Text.of("Gain ") {
                color = TextColor.GRAY
                append("+10%", TextColor.GREEN)
                append(" more ")
                append("Mithril Powder", TextColor.DARK_GREEN)
            },
        ),
    ),

    CRYSTAL_HOLLOWS(
        island = SkyBlockIsland.CRYSTAL_HOLLOWS,
        mild = SkyBlockWeather(
            type = BREEZE,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 100.0,
                SkyBlockStat.MINING_FORTUNE to 25.0,
                SkyBlockStat.FISHING_SPEED to 25.0,
            ),
        ),
        extreme = SkyBlockWeather(
            type = ROCKFALL,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 250.0,
                SkyBlockStat.MINING_FORTUNE to 50.0,
                SkyBlockStat.FISHING_SPEED to 50.0,
            ),
            specialEffect = Text.of("Gain ") {
                color = TextColor.GRAY
                append("+10%", TextColor.GREEN)
                append(" more ")
                append("Gemstone Powder", TextColor.PINK)
            },
        ),
    ),

    GLACITE_TUNNELS(
        island = SkyBlockIsland.MINESHAFT,
        mild = SkyBlockWeather(
            type = BREEZE,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 100.0,
                SkyBlockStat.MINING_FORTUNE to 25.0,
                SkyBlockStat.FISHING_SPEED to 25.0,
            ),
        ),
        extreme = SkyBlockWeather(
            type = SNOWSTORM,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 250.0,
                SkyBlockStat.MINING_FORTUNE to 50.0,
                SkyBlockStat.FISHING_SPEED to 50.0,
            ),
            specialEffect = Text.of("Gain ") {
                color = TextColor.GRAY
                append("+10%", TextColor.GREEN)
                append(" more ")
                append("Glacite Powder", TextColor.AQUA)
            },
        ),
    ),

    SPIDERS_DEN(
        island = SkyBlockIsland.SPIDERS_DEN,
        mild = SkyBlockWeather(
            type = RAIN,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.COMBAT_WISDOM to 5.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 2.5,
            ),
            specialEffect = Text.of {
                color = TextColor.GRAY
                append("Rain Slimes", TextColor.GREEN)
                append(" spawn around the island")
            },
        ),
        extreme = SkyBlockWeather(
            type = THUNDERSTORM,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 50.0,
                SkyBlockStat.COMBAT_WISDOM to 10.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 5.0,
            ),
            specialEffect = Text.of {
                color = TextColor.GRAY
                append("Toxic Rain Slimes", TextColor.GREEN)
                append(" spawn around the island")
            },
        ),
    ),

    THE_END(
        island = SkyBlockIsland.THE_END,
        mild = SkyBlockWeather(
            type = WISPFALL,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.MINING_FORTUNE to 25.0,
                SkyBlockStat.COMBAT_WISDOM to 5.0,
                SkyBlockStat.TRACKING to 1.0,
            ),
        ),
        extreme = SkyBlockWeather(
            type = VOIDSTORM,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.MINING_FORTUNE to 50.0,
                SkyBlockStat.COMBAT_WISDOM to 10.0,
                SkyBlockStat.TRACKING to 2.5,
            ),
            specialEffect = Text.of {
                color = TextColor.GRAY
                append("Superior Dragons", TextColor.YELLOW)
                append(" are ")
                append("1%", TextColor.GREEN)
                append(" more likely to spawn")
            },
        ),
    ),

    CRIMSON_ISLE(
        island = SkyBlockIsland.CRIMSON_ISLE,
        mild = SkyBlockWeather(
            type = ASHFALL,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.TROPHY_CHANCE to 2.5,
                SkyBlockStat.COMBAT_WISDOM to 5.0,
            ),
        ),
        extreme = SkyBlockWeather(
            type = HELLSTORM,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 50.0,
                SkyBlockStat.TROPHY_CHANCE to 5.0,
                SkyBlockStat.COMBAT_WISDOM to 10.0,
            ),
            specialEffect = Text.of {
                color = TextColor.GRAY
                append("Trophy Fish", TextColor.GOLD)
                append(" are ")
                append("5%", TextColor.GREEN)
                append(" more likely to be ")
                append("GOLD", TextColor.GOLD)
                append(" or ")
                append("DIAMOND", TextColor.AQUA)
            },
        ),
    ),

    MOONGLADE_MARSH(
        island = SkyBlockIsland.GALATEA,
        mild = SkyBlockWeather(
            type = MOONFALL,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.FORAGING_FORTUNE to 10.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 2.5,
            ),
        ),
        extreme = SkyBlockWeather(
            type = THUNDERSTORM,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 50.0,
                SkyBlockStat.FORAGING_FORTUNE to 25.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 5.0,
            ),
            specialEffect = Text.of("Gain ") {
                color = TextColor.GRAY
                append("+10%", TextColor.GREEN)
                append(" more ")
                append("Forest Whispers", TextColor.DARK_AQUA)
            },
        ),
    ),

    BACKWATER_BAYOU(
        island = SkyBlockIsland.BACKWATER_BAYOU,
        mild = SkyBlockWeather(
            type = SMOG,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.TREASURE_CHANCE to 1.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 2.5,
            ),
        ),
        extreme = SkyBlockWeather(
            type = ACID_RAIN,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 50.0,
                SkyBlockStat.TREASURE_CHANCE to 2.5,
                SkyBlockStat.SEA_CREATURE_CHANCE to 5.0,
            ),
            specialEffect = Text.of("Gain a ") {
                color = TextColor.GRAY
                append("+20%", TextColor.GREEN)
                append(" chance to catch ")
                append("2", TextColor.GREEN)
                append(" pieces of ")
                append("Junk", TextColor.DARK_GREEN)
                append(" at once!")
            },
        ),
    ),

    LOTUS_ATOLL(
        island = SkyBlockIsland.LOTUS_ATOLL,
        mild = SkyBlockWeather(
            type = TROPICAL_RAIN,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.TROPHY_CHANCE to 2.5,
                SkyBlockStat.SEA_CREATURE_CHANCE to 2.5,
            ),
        ),
        extreme = SkyBlockWeather(
            type = BLOSSOMING,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 50.0,
                SkyBlockStat.TROPHY_CHANCE to 5.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 5.0,
            ),
            specialEffect = Text.of {
                color = TextColor.GRAY
                append("Trophy Frogs", TextColor.DARK_GREEN)
                append(" are ")
                append("5%", TextColor.GREEN)
                append(" more likely to be ")
                append("GOLD", TextColor.GOLD)
                append(" or ")
                append("DIAMOND", TextColor.AQUA)
            },
        ),
    ),

    GARDEN(
        island = SkyBlockIsland.GARDEN,
        mild = SkyBlockWeather(
            type = RAIN,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FARMING_FORTUNE to 25.0,
                SkyBlockStat.BONUS_PEST_CHANCE to 5.0,
                SkyBlockStat.OVERBLOOM to 2.5,
            ),
        ),
        extreme = SkyBlockWeather(
            type = BLOOMING,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.FARMING_FORTUNE to 50.0,
                SkyBlockStat.BONUS_PEST_CHANCE to 10.0,
                SkyBlockStat.OVERBLOOM to 5.0,
            ),
            specialEffect = Text.of("Gain ") {
                color = TextColor.GRAY
                append("+10%", TextColor.GREEN)
                append(" more ")
                append("Sowdust", TextColor.DARK_GREEN)
            },
        ),
    ),

    JERRYS_WORKSHOP(
        island = SkyBlockIsland.JERRYS_WORKSHOP,
        mild = SkyBlockWeather(
            type = BREEZE,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 5.0,
                SkyBlockStat.TREASURE_CHANCE to 2.5,
            ),
        ),
        extreme = SkyBlockWeather(
            type = BLIZZARD,
            intensity = EXTREME,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 50.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 10.0,
                SkyBlockStat.TREASURE_CHANCE to 5.0,
            ),
            specialEffect = Text.of("Gain ") {
                color = TextColor.GRAY
                append("+10%", TextColor.GREEN)
                append(" more ")
                append("Ice Essence", TextColor.AQUA)
            },
        ),
    );

    companion object {
        fun getGroupFor(island: SkyBlockIsland): SkyBlockWeatherGroup? = entries.find { it.island == island }

        fun getCurrentGroup(): SkyBlockWeatherGroup? {
            if (GlaciteAPI.inGlaciteTunnels()) return GLACITE_TUNNELS
            return entries.find { it != GLACITE_TUNNELS && it.island.inIsland() }
        }
    }
}
