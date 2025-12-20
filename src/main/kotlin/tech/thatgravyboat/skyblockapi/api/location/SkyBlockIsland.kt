package tech.thatgravyboat.skyblockapi.api.location

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class SkyBlockIsland(val id: String, displayName: String? = null) {
    PRIVATE_ISLAND("dynamic"),
    HUB("hub"),
    DUNGEON_HUB("dungeon_hub"),
    THE_BARN("farming_1", "The Farming Islands"),
    THE_PARK("foraging_1"),
    GOLD_MINES("mining_1", "Gold Mine"),
    DEEP_CAVERNS("mining_2"),
    DWARVEN_MINES("mining_3"),
    CRYSTAL_HOLLOWS("crystal_hollows"),
    MINESHAFT("mineshaft"),
    SPIDERS_DEN("combat_1", "Spider's Den"),
    THE_END("combat_3"),
    CRIMSON_ISLE("crimson_isle"),
    GARDEN("garden"),
    BACKWATER_BAYOU("fishing_1"),
    GALATEA("foraging_2"),

    THE_RIFT("rift"),
    DARK_AUCTION("dark_auction"),
    THE_CATACOMBS("dungeon"),
    KUUDRA("kuudra"),
    JERRYS_WORKSHOP("winter", "Jerry's Workshop"),
    ;

    fun inIsland() = if (LocationAPI.skyblockIslandOverride == null) LocationAPI.island == this else LocationAPI.skyblockIslandOverride == this

    val displayName = displayName ?: toFormattedName()

    override fun toString() = displayName

    companion object {
        fun getById(input: String) = entries.firstOrNull { it.id == input }

        fun inAnyIsland(vararg islands: SkyBlockIsland) = islands.any { it.inIsland() }

        fun inAnyIsland(islands: Collection<SkyBlockIsland>) = islands.any { it.inIsland() }
    }
}
