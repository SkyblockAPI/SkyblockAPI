package tech.thatgravyboat.skyblockapi.api.location

enum class SkyblockIsland(val id: String) {
    PRIVATE_ISLAND("dynamic"),
    HUB("hub"),
    DUNGEON_HUB("dungeon_hub"),
    THE_BARN("farming_1"),
    THE_PARK("foraging_1"),
    GOLD_MINES("mining_1"),
    DEEP_CAVERNS("mining_2"),
    DWARVEN_MINES("mining_3"),
    CRYSTAL_HOLLOWS("crystal_hollows"),
    MINESHAFT("mineshaft"),
    SPIDERS_DEN("combat_1"),
    THE_END("combat_3"),
    CRIMSON_ISLE("crimson_isle"),
    GARDEN("garden"),

    THE_RIFT("rift"),
    DARK_AUCTION("dark_auction"),
    THE_CATACOMBS("dungeon"),
    JERRYS_WORKSHOP("winter"),
    ;

    fun inIsland() = LocationAPI.island == this

    val formattedName = name.split('_').joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

    companion object {

        fun getById(input: String) = entries.firstOrNull { it.id == input }

        fun inAnyIsland(vararg islands: SkyblockIsland) = islands.any { it.inIsland() }

        fun inAnyIsland(islands: Collection<SkyblockIsland>) = islands.any { it.inIsland() }
    }
}
