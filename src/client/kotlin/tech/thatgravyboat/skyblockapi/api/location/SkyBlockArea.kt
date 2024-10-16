package tech.thatgravyboat.skyblockapi.api.location

data class SkyBlockArea(val name: String) {
    fun inArea() = LocationAPI.area == this

    companion object {

        fun inAnyArea(vararg areas: SkyBlockArea) = LocationAPI.area in areas
    }
}

@Suppress("unused")
object SkyBlockAreas {

    private val registeredAreas = mutableMapOf<String, SkyBlockArea>()

    private fun register(key: String, name: String) = registeredAreas.getOrPut(key) { SkyBlockArea(name) }

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

    // Rift
    val WYLD_WOODS = register("wyld_woods", "Wyld Woods")
    val THE_BASTION = register("the_bastion", "The Bastion")
    val BROKEN_CAGE = register("broken_cage", "Broken Cage")
    val SHIFTED_TAVERN = register("shifted_tavern", "Shifted Tavern")
    val BLACK_LAGOON = register("black_lagoon", "Black Lagoon")
    val LAGOON_CAVE = register("lagoon_cave", "Lagoon Cave")
    val OTHERSIDE = register("otherside", "Otherside")
    val LEECHES_LAIR = register("leeches_lair", "Leeches Lair")
    val LAGOON_HUT = register("lagoon_hut", "Lagoon Hut")
    val AROUND_COLOSSEUM = register("around_colosseum", "Around Colosseum")
    val WEST_VILLAGE = register("west_village", "West Village")
    val DOPLHIN_TRAINER = register("dolphin_trainer", "Dolphin Trainer")
    val INFESTED_HOUSE = register("infested_house", "Infested House")
    val DREADFARM = register("dreadfarm", "Dreadfarm")
    val MIRRORVERSE = register("mirrorverse", "Mirrorverse")
    val CAKE_HOUSE = register("cake_house", "Cake House")
    val VILLAGE_PLAZA = register("village_plaza", "Village Plaza")
    val MURDER_HOUSE = register("murder_house", "Murder House")
    val TAYLORS = register("taylors", "Taylor's")
    val HALF_EATEN_CAVE = register("half_eaten_cave", "Half-Eaten Cave")
    val BOOK_IN_A_BOOK = register("book_in_a_book", "Book in a Book")
    val EMPTY_BANK = register("empty_bank", "Empty Bank")
    val BARRIER_STREET = register("barrier_street", "Barrier Street")
    val BARRY_CENTER = register("barry_center", "Barry Center")
    val BARRY_HQ = register("barry_hq", "Barry HQ")
    val RIFT_GALLERY = register("rift_gallery", "Rift Gallery")
    val RIFT_GALLERY_ENTRANCE = register("rift_gallery_entrance", "Rift Gallery Entrance")

    // Rift-Slayer
    val PHOTON_PATHWAY = register("photon_pathway", "Photon Pathway")
    val STILLGORE_CHATEAU = register("stillgore_chateau", "Stillgore Château")
    val OUBLIETTE = register("oubliette", "Oubliette")
    val FAIRYLOSOPHER_TOWER = register("fairylosopher_tower", "Fairylosopher Tower")

    // Dwarves
    val BASECAMP = register("basecamp", "Dwarven Base Camp")
    val FOSSIL_RESEARCH = register("fossil_research", "Fossil Research Center")
    val GLACITE_TUNNELS = register("glacite_tunnels", "Glacite Tunnels")
    val GREAT_LAKE = register("great_lake", "Great Glacite Lake")

    // Crimson
    val DOJO = register("dojo", "Dojo")
    val DOJO_ARENA = register("dojo_arena", "Dojo Arena")
    val MAGMA_CHAMBER = register("magma_chamber", "Magma Chamber")
}
