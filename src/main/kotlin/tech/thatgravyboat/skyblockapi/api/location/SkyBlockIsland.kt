package tech.thatgravyboat.skyblockapi.api.location

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

public enum class SkyBlockIsland(public val id: String, displayName: String? = null) {
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
    LOTUS_ATOLL("lotus_atoll"),
    GALATEA("foraging_2", "Moonglade Marsh"),
    TORRHUS_CANYON("foraging_3"),
    SAFARI("safari", "Critter Safari"),

    THE_RIFT("rift"),
    DARK_AUCTION("dark_auction"),
    THE_CATACOMBS("dungeon"),
    KUUDRA("kuudra"),
    JERRYS_WORKSHOP("winter", "Jerry's Workshop"),
    ;

    public fun inIsland(): Boolean = LocationAPI.island == this

    public val displayName: String = displayName ?: toFormattedName()

    override fun toString(): String = displayName

    public companion object {

        public fun getById(input: String): SkyBlockIsland? = entries.firstOrNull { it.id == input }

        public fun inAnyIsland(vararg islands: SkyBlockIsland): Boolean = LocationAPI.island in islands

        public fun inAnyIsland(islands: Collection<SkyBlockIsland>): Boolean = LocationAPI.island in islands
    }
}
