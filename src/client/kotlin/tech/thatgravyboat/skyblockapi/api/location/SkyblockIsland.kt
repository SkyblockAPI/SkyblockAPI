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
    SPIDERS_DEN("combat_1"),
    THE_END("combat_3"),
    CRIMSON_ISLE("crimson_isle"),
    GARDEN("garden"),

    THE_RIFT("rift"),
    DARK_AUCTION("dark_auction"),
    THE_CATACOMBS("dungeon"),
    JERRYS_WORKSHOP("winter"),
    ;

    companion object {

        fun getById(input: String) = entries.firstOrNull { it.id == input }
    }
}
