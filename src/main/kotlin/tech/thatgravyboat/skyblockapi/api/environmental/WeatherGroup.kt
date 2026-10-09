package tech.thatgravyboat.skyblockapi.api.environmental

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

public enum class WeatherIntensity(public val color: Int, public val displayName: String) {
    MILD(TextColor.YELLOW, "Mild"),
    EXTREME(TextColor.RED, "EXTREME"),
    ;

    public val component: MutableComponent = Text.of(displayName, color)
}

public enum class WeatherType(public val icon: Char, public val color: Int, weatherName: String? = null) {
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

    public val weatherName: String = weatherName ?: toFormattedName()
    public val component: MutableComponent = Text.of("$icon ${this.weatherName}", color)
    public val iconComponent: MutableComponent = Text.of(icon.toString(), color)
}

public data class WeatherEvent(
    val type: WeatherType,
    val intensity: WeatherIntensity,
    val bonuses: Map<SkyBlockStat, Double>,
    val specialEffect: Component? = null,
)

public enum class WeatherGroup(
    public val island: SkyBlockIsland,
    public val areas: List<SkyBlockArea> = emptyList(),
    nameOverride: String? = null,
    public val mild: WeatherEvent,
    public val extreme: WeatherEvent,
) {
    DWARVEN_MINES(
        island = SkyBlockIsland.DWARVEN_MINES,
        mild = WeatherEvent(
            type = BREEZE,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 100.0,
                SkyBlockStat.MINING_FORTUNE to 25.0,
                SkyBlockStat.FISHING_SPEED to 25.0,
            ),
        ),
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
            type = BREEZE,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 100.0,
                SkyBlockStat.MINING_FORTUNE to 25.0,
                SkyBlockStat.FISHING_SPEED to 25.0,
            ),
        ),
        extreme = WeatherEvent(
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
        island = SkyBlockIsland.DWARVEN_MINES,
        areas = listOf(
            SkyBlockAreas.GLACITE_TUNNELS,
            SkyBlockAreas.GREAT_LAKE,
            SkyBlockAreas.BASECAMP,
            SkyBlockAreas.FOSSIL_RESEARCH,
        ),
        nameOverride = "Glacite Tunnels",
        mild = WeatherEvent(
            type = BREEZE,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.MINING_SPEED to 100.0,
                SkyBlockStat.MINING_FORTUNE to 25.0,
                SkyBlockStat.FISHING_SPEED to 25.0,
            ),
        ),
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
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
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
            type = WISPFALL,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.MINING_FORTUNE to 25.0,
                SkyBlockStat.COMBAT_WISDOM to 5.0,
                SkyBlockStat.TRACKING to 1.0,
            ),
        ),
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
            type = ASHFALL,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.TROPHY_CHANCE to 2.5,
                SkyBlockStat.COMBAT_WISDOM to 5.0,
            ),
        ),
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
            type = MOONFALL,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.FORAGING_FORTUNE to 10.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 2.5,
            ),
        ),
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
            type = SMOG,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.TREASURE_CHANCE to 1.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 2.5,
            ),
        ),
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
            type = TROPICAL_RAIN,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.TROPHY_CHANCE to 2.5,
                SkyBlockStat.SEA_CREATURE_CHANCE to 2.5,
            ),
        ),
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
            type = RAIN,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FARMING_FORTUNE to 25.0,
                SkyBlockStat.BONUS_PEST_CHANCE to 5.0,
                SkyBlockStat.OVERBLOOM to 2.5,
            ),
        ),
        extreme = WeatherEvent(
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
        mild = WeatherEvent(
            type = BREEZE,
            intensity = MILD,
            bonuses = mapOf(
                SkyBlockStat.FISHING_SPEED to 25.0,
                SkyBlockStat.SEA_CREATURE_CHANCE to 5.0,
                SkyBlockStat.TREASURE_CHANCE to 2.5,
            ),
        ),
        extreme = WeatherEvent(
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

    public val formattedName: String = nameOverride ?: toFormattedName()
    override fun toString(): String = formattedName

    public fun inLocation(): Boolean = island.inIsland() && (areas.isEmpty() || SkyBlockArea.inAnyArea(areas))

    public companion object {
        public fun getGroupsFor(island: SkyBlockIsland): List<WeatherGroup> = entries.filter { it.island == island }
        public fun getGroupFor(island: SkyBlockIsland, area: SkyBlockArea?): WeatherGroup? = entries.find { it.island == island && (area == null || area in it.areas) }

        public fun getCurrentGroup(): WeatherGroup? {
            val activeGroups = entries.filter { it.inLocation() }
            // If multiple matches, pick the group that's more specific
            return activeGroups.find { it.areas.isNotEmpty() } ?: activeGroups.firstOrNull()
        }
    }
}
