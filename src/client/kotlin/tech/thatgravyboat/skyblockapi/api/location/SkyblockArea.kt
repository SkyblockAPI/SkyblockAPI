package tech.thatgravyboat.skyblockapi.api.location

data class SkyblockArea(val name: String)

object SkyBlockAreas {

    private val registeredAreas = mutableMapOf<String, SkyblockArea>()

    private fun register(key: String, name: String) = registeredAreas.computeIfAbsent(key) { SkyblockArea(name) }

    val NONE = register("none", "None")
    val PRIVATE_ISLAND = register("private_island", "Your Island")

    // Hub
    val VILLAGE = register("village", "Village")
    val FOREST = register("forest", "Forest")
    val PET_CARE = register("pet_care", "Pet Care")
    val FARM = register("farm", "Farm")
    val ARTISTS_ABODE = register("artists_abode", "Artist's Abode")
    val COLOSSEUM = register("colosseum", "Colosseum")
    val FASHION_SHOP = register("fashion_shop", "Fashion Shop")
    val FLOWER_HOUSE = register("flower_house", "Flower House")
    val CANVAS_ROOM = register("canvas_room", "Canvas Room")
    val MOUNTAIN = register("mountain", "Mountain")
    val BANK = register("bank", "Bank")
    val AUCTION_HOUSE = register("auction_house", "Auction House")
    val SHENS_AUCTION = register("shens_auction", "Shen's Auction")
    val COMMUNITY_CENTER = register("community_center", "Community Center")
    val ELECTION_ROOM = register("election_room", "Election Room")
    val FARMHOUSE = register("farmhouse", "Farmhouse")
    val WEAPONSMITH = register("weaponsmith", "Weaponsmith")
    val BLACKSMITH = register("blacksmith", "Blacksmith")
    val ARCHERY_RANGE = register("archery_range", "Archery Range")
    val LIBRARY = register("library", "Library")
    val HEXATORUM = register("hexatorium", "Hexatorium")
    val TRADE_CENTER = register("trade_center", "Trade Center")
    val BUILDERS_HOUSE = register("builders_house", "Builder's House")
    val TAVERN = register("tavern", "Tavern")
    val GRAVEYARD = register("graveyard", "Graveyard")
    val COAL_MINE = register("coal_mine", "Coal Mine")
    val BAZAAR_ALLEY = register("bazaar_alley", "Bazaar Alley")
    val WILDERNESS = register("wilderness", "Wilderness")
    val FISHERMANS_HUT = register("fishermans_hut", "Fisherman's Hut")
    val UNINCORPORATED = register("unincorporated", "Unincorporated")
    val WIZARD_TOWER = register("wizard_tower", "Wizard Tower")
    val RUINS = register("ruins", "Ruins")

}
