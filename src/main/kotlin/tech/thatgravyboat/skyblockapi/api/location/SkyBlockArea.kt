package tech.thatgravyboat.skyblockapi.api.location

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
public data class SkyBlockArea(val name: String) {
    public fun inArea() = LocationAPI.area == this

    companion object {

        public fun inAnyArea(vararg areas: SkyBlockArea) = LocationAPI.area in areas
        public fun inAnyArea(areas: Collection<SkyBlockArea>) = LocationAPI.area in areas
    }
}

@Suppress("unused")
public object SkyBlockAreas {

    internal val registeredAreas = mutableMapOf<String, SkyBlockArea>()

    private fun register(key: String, name: String) = registeredAreas.getOrPut(key) { SkyBlockArea(name) }

    public val NONE = register("none", "None")
    public val PRIVATE_ISLAND = register("private_island", "Your Island")
    public val GARDEN = register("garden", "The Garden")

    // Hub
    public val VILLAGE = register("village", "Village")
    public val FOREST = register("forest", "Forest")
    public val PET_CARE = register("pet_care", "Pet Care")
    public val FARM = register("farm", "Farm")
    public val ARTISTS_ABODE = register("artists_abode", "Artist's Abode")
    public val COLOSSEUM = register("colosseum", "Colosseum")
    public val FASHION_SHOP = register("fashion_shop", "Fashion Shop")
    public val FLOWER_HOUSE = register("flower_house", "Flower House")
    public val CANVAS_ROOM = register("canvas_room", "Canvas Room")
    public val MOUNTAIN = register("mountain", "Mountain")
    public val BANK = register("bank", "Bank")
    public val AUCTION_HOUSE = register("auction_house", "Auction House")
    public val SHENS_AUCTION = register("shens_auction", "Shen's Auction")
    public val COMMUNITY_CENTER = register("community_center", "Community Center")
    public val ELECTION_ROOM = register("election_room", "Election Room")
    public val FARMHOUSE = register("farmhouse", "Farmhouse")
    public val WEAPONSMITH = register("weaponsmith", "Weaponsmith")
    public val BLACKSMITH = register("blacksmith", "Blacksmith")
    public val ARCHERY_RANGE = register("archery_range", "Archery Range")
    public val LIBRARY = register("library", "Library")
    public val HEXATORUM = register("hexatorium", "Hexatorium")
    public val TRADE_CENTER = register("trade_center", "Trade Center")
    public val BUILDERS_HOUSE = register("builders_house", "Builder's House")
    public val TAVERN = register("tavern", "Tavern")
    public val GRAVEYARD = register("graveyard", "Graveyard")
    public val COAL_MINE = register("coal_mine", "Coal Mine")
    public val BAZAAR_ALLEY = register("bazaar_alley", "Bazaar Alley")
    public val WILDERNESS = register("wilderness", "Wilderness")
    public val FISHING_OUTPOST = register("fishing_outpost", "Fishing Outpost")
    public val FISHERMANS_HUT = register("fishermans_hut", "Fisherman's Hut")
    public val UNINCORPORATED = register("unincorporated", "Unincorporated")
    public val WIZARD_TOWER = register("wizard_tower", "Wizard Tower")
    public val RUINS = register("ruins", "Ruins")

    // Rift
    public val WYLD_WOODS = register("wyld_woods", "Wyld Woods")
    public val THE_BASTION = register("the_bastion", "The Bastion")
    public val BROKEN_CAGE = register("broken_cage", "Broken Cage")
    public val SHIFTED_TAVERN = register("shifted_tavern", "Shifted Tavern")
    public val BLACK_LAGOON = register("black_lagoon", "Black Lagoon")
    public val LAGOON_CAVE = register("lagoon_cave", "Lagoon Cave")
    public val OTHERSIDE = register("otherside", "Otherside")
    public val LEECHES_LAIR = register("leeches_lair", "Leeches Lair")
    public val LAGOON_HUT = register("lagoon_hut", "Lagoon Hut")
    public val AROUND_COLOSSEUM = register("around_colosseum", "Around Colosseum")
    public val WEST_VILLAGE = register("west_village", "West Village")
    public val DOPLHIN_TRAINER = register("dolphin_trainer", "Dolphin Trainer")
    public val INFESTED_HOUSE = register("infested_house", "Infested House")
    public val DREADFARM = register("dreadfarm", "Dreadfarm")
    public val MIRRORVERSE = register("mirrorverse", "Mirrorverse")
    public val CAKE_HOUSE = register("cake_house", "Cake House")
    public val VILLAGE_PLAZA = register("village_plaza", "Village Plaza")
    public val MURDER_HOUSE = register("murder_house", "Murder House")
    public val TAYLORS = register("taylors", "Taylor's")
    public val HALF_EATEN_CAVE = register("half_eaten_cave", "Half-Eaten Cave")
    public val BOOK_IN_A_BOOK = register("book_in_a_book", "Book in a Book")
    public val EMPTY_BANK = register("empty_bank", "Empty Bank")
    public val BARRIER_STREET = register("barrier_street", "Barrier Street")
    public val BARRY_CENTER = register("barry_center", "Barry Center")
    public val BARRY_HQ = register("barry_hq", "Barry HQ")
    public val RIFT_GALLERY = register("rift_gallery", "Rift Gallery")
    public val RIFT_GALLERY_ENTRANCE = register("rift_gallery_entrance", "Rift Gallery Entrance")
    public val THE_MOUNTAINTOP = register("the_mountaintop", "The Mountaintop")
    public val WIZARDMAN_BUREAU = register("wizardman_bureau", "Wizardman Bureau")
    public val THE_VENTS = register("the_vents", "The Vents")
    public val CEREBRAL_CITADEL = register("cerebral_citadel", "Cerebral Citadel")
    public val WALK_OF_FAME = register("walk_of_fame", "Walk of Fame")
    public val TRIAL_GROUNDS = register("trial_grounds", "Trial Grounds")
    public val CONTINUUM = register("continuum", "Continuum")
    public val TIME_CHAMBER = register("time_chamber", "Time Chamber")

    // Rift-Slayer
    public val PHOTON_PATHWAY = register("photon_pathway", "Photon Pathway")
    public val STILLGORE_CHATEAU = register("stillgore_chateau", "Stillgore Château")
    public val OUBLIETTE = register("oubliette", "Oubliette")
    public val FAIRYLOSOPHER_TOWER = register("fairylosopher_tower", "Fairylosopher Tower")

    // Dwarves
    public val BASECAMP = register("basecamp", "Dwarven Base Camp")
    public val FOSSIL_RESEARCH = register("fossil_research", "Fossil Research Center")
    public val GLACITE_TUNNELS = register("glacite_tunnels", "Glacite Tunnels")
    public val GREAT_LAKE = register("great_lake", "Great Glacite Lake")

    // Crimson
    public val DOJO = register("dojo", "Dojo")
    public val DOJO_ARENA = register("dojo_arena", "Dojo Arena")
    public val MAGMA_CHAMBER = register("magma_chamber", "Magma Chamber")
    public val CRIMSON_ISLE = register("crimson_isle", "Crimson Isle")
    public val CRIMSON_FIELDS = register("crimson_fields", "Crimson Fields")
    public val BURNING_DESERT = register("burning_desert", "Burning Desert")
    public val DRAGONTAIL = register("dragontail", "Dragontail")
    public val DRAGONTAIL_BLACKSMITH = register("dragontail_blacksmith", "Dragontail Blacksmith")
    public val DRAGONTAIL_BANK = register("dragontail_bank", "Dragontail Bank")
    public val DRAGONTAIL_TOWNSQUARE = register("dragontail_townsquare", "Dragontail Townsquare")
    public val DRAGONTAIL_AUCTION_HOUS = register("dragontail_auction_hous", "Dragontail Auction Hous")
    public val MINION_SHOP = register("minion_shop", "Minion Shop")
    public val THE_DUKEDOM = register("the_dukedom", "The Dukedom")
    public val BLAZING_VOLCANO = register("blazing_volcano", "Blazing Volcano")
    public val ODGER_HUT = register("odger_hut", "Odger's Hut")
    public val THE_WASTELAND = register("the_wasteland", "The Wasteland")
    public val FORGOTTEN_SKULL = register("forgotten_skull", "Forgotten Skull")
    public val SCARLETON = register("scarleton", "Scarleton")
    public val COURTYARD = register("courtyard", "Courtyard")
    public val SCARLETON_BANK = register("scarleton_bank", "Scarleton Bank")
    public val SCARLETON_PLAZA = register("scarleton_plaza", "Scarleton Plaza")
    public val SCARLETON_AUCTION_HOUSE = register("scarleton_auction_house", "Scarleton Auction House")
    public val SCARLETON_BAZAAR = register("scarleton_bazaar", "Scarleton Bazaar")
    public val SCARLETON_MINION_SHOP = register("scarleton_minion_shop", "Scarleton Minion Shop")
    public val SCARLETON_BLACKSMITH = register("scarleton_blacksmith", "Scarleton Blacksmith")
    public val CATHEDRAL = register("cathedral", "Cathedral")
    public val MYSTIC_MARSH = register("mystic_marsh", "Mystic Marsh")
    public val MATRIARCH_LAIR = register("matriarch_lair", "Matriarch's Lair")
    public val BELLY_OF_THE_BEAST = register("belly_of_the_beast", "Belly of the Beast")
    public val SMOLDERING_TOMB = register("smoldering_tomb", "Smoldering Tomb")


    // Jerry
    public val GLACIAL_CAVE = register("glacial_cave", "Glacial Cave")
    public val MOUNT_JERRY = register("mount_jerry", "Mount Jerry")
    public val HOT_SPRINGS = register("hot_springs", "Hot Springs")
    public val JERRY_POND = register("jerry_pond", "Jerry Pond")
    public val REFLECTIVE_POND = register("reflective_pond", "Reflective Pond")
    public val TERRYS_SHACK = register("terrys_shack", "Terry's Shack")
    public val SUNKEN_JERRY_POND = register("sunken_jerry_pond", "Sunken Jerry Pond")
    public val EINARYS_EMPORIUM = register("einarys_emporioum", "Einary's Emporium")
    public val SHERRYS_SHOWROOM = register("sherrys_showroom", "Sherry's Showroom")
    public val GARYS_SHACK = register("garys_shack", "Gary's Shack")

    // Spider
    public val SPIDER_MOUND = register("spider_mound", "Spider Mound")
    public val GRAVEL_MINES = register("gravel_mines", "Gravel Mines")
    public val GRANDMAS_HOUSE = register("grandmas_house", "Grandma's House")
    public val ARACHNES_BURROW = register("arachnes_burrow", "Arachne's Burrow")
    public val ARACHNES_SANCTUARY = register("arachnes_sanctuary", "Arachne's Sanctuary")
    public val ARCHAEOLOGISTS_CAMP = register("archaeologists_camp", "Archaeologist's Camp")

    // End
    public val DRAGONS_NEST = register("dragons_nest", "Dragon's Nest")
    public val VOID_SEPULTURE = register("void_sepulture", "Void Sepulture")
    public val VOID_SLATE = register("void_slate", "Void Slate")
    public val ZEALOT_BRUISER_HIDEOUT = register("zealot_bruiser_hideout", "Zealot Bruiser Hideout")

    // Farming Islands
    public val THE_BARN = register("the_barn", "The Barn")
    public val MUSHROOM_DESERT = register("mushroom_desert", "Mushroom Desert")
    public val WINDMILL = register("windmill", "Windmill")
    public val DESERT_SETTLEMENT = register("desert_settlement", "Desert Settlement")
    public val GLOWING_MUSHROOM_CAVE = register("glowing_mushroom_cave", "Glowing Mushroom Cave")
    public val JAKES_HOUSE = register("jakes_house", "Jake's House")
    public val MUSHROOM_GORGE = register("mushroom_gorge", "Mushroom Gorge")
    public val OASIS = register("oasis", "Oasis")
    public val OVERGROWN_MUSHROOM_CAVE = register("overgrown_mushroom_cave", "Overgrown Mushroom Cave")
    public val SHEPHERDS_KEEP = register("shepherds_keep", "Shepherd's Keep")
    public val TRAPPERS_DEN = register("trappers_den", "Trapper's Den")
    public val TREASURE_HUNTER_CAMP = register("treasure_hunter_camp", "Treasure Hunter Camp")

    // Park
    public val BIRCH_PARK = register("birch_park", "Birch Park")
    public val HOWLING_CAVE = register("howling_cave", "Howling Cave")
    public val SOUL_CAVE = register("soul_cave", "Soul Cave")
    public val SPIRIT_CAVE = register("spirit_cave", "Spirit Cave")
    public val SPRUCE_WOODS = register("spruce_woods", "Spruce Woods")
    public val LONELY_ISLAND = register("lonely_island", "Lonely Island")
    public val VIKING_LONGHOUSE = register("viking_longhouse", "Viking Longhouse")
    public val DARK_THICKET = register("dark_thicket", "Dark Thicket")
    public val SAVANNA_WOODLAND = register("savanna_woodland", "Savanna Woodland")
    public val MELODYS_PLATEAU = register("melodys_plateau", "Melody's Plateau")
    public val JUNGLE_ISLAND = register("jungle_island", "Jungle Island")

    // Deep Caverns
    public val DEEP_CAVERNS = register("deep_caverns", "Deep Caverns")
    public val GUNPOWDER_MINES = register("gunpowder_mines", "Gunpowder Mines")
    public val LAPIS_QUARRY = register("lapis_quarry", "Lapis Quarry")
    public val PIGMENS_DEN = register("pigmens_den", "Pigmen's Den")
    public val SLIMEHILL = register("slimehill", "Slimehill")
    public val DIAMOND_RESERVE = register("diamond_reserve", "Diamond Reserve")
    public val OBSIDIAN_SANCTUARY = register("obsidian_sanctuary", "Obsidian Sanctuary")

    // Crystal Hollows
    public val CRYSTAL_NUCLEUS = register("crystal_nucleus", "Crystal Nucleus")
    public val GOBLIN_HOLDOUT = register("goblin_holdout", "Goblin Holdout")
    public val GOBLIN_QUEENS_DEN = register("goblin_queens_den", "Goblin Queen's Den")
    public val JUNGLE = register("jungle", "Jungle")
    public val JUNGLE_TEMPLE = register("jungle_temple", "Jungle Temple")
    public val PRECURSOR_REMNANTS = register("precursor_remnants", "Precursor Remnants")
    public val LOST_PRECURSOR_CITY = register("lost_precursor_city", "Lost Precursor City")
    public val MITHRIL_DEPOSITS = register("mithril_deposits", "Mithril Deposits")
    public val DRAGONS_LAIR = register("dragons_lair", "Dragon's Lair") // also in galatea
    public val MINES_OF_DIVAN = register("mines_of_divan", "Mines of Divan")
    public val MAGMA_FIELDS = register("magma_fields", "Magma Fields")
    public val KHAZAD_DUM = register("khazad_dum", "Khazad-dûm")
    public val FAIRY_GROTTO = register("fairy_grotto", "Fairy Grotto")

    // Backwater Bayou
    public val BACKWATER_BAYOU = register("backwater_bayou", "Backwater Bayou") // The full island uses this

    // Lotus Atoll
    public val LOTUS_ATOLL = register("lotus_atoll", "Lotus Atoll")
    public val LOTUS_EATERS_CAVE = register("lotus_eaters_cave", "Lotus Eater's Cave")
    public val LOTUS_HIGHLANDS = register("lotus_highlands", "Lotus Highlands")

    // Galatea
    public val TANGLEBURG_PATH = register("tangleburg_path", "Tangleburg's Path")
    public val TANGLEBURG = register("tangleburg", "Tangleburg")
    public val NORTH_REACHES = register("north_reaches", "North Reaches")
    public val WEST_REACHES = register("west_reaches", "West Reaches")
    public val SOUTH_REACHES = register("south_reaches", "South Reaches")
    public val MOONGLADE_MARSH = register("moonglade_marsh", "Moonglade Marsh")
    public val MOONGLADE_EDGE = register("moonglade_edge", "Moonglade's Edge")
    public val VERDANT_SUMMIT = register("verdant_summit", "Verdant Summit")
    public val NORTH_WETLANDS = register("north_wetlands", "North Wetlands")
    public val WESTBOUND_WETLANDS = register("westbound_wetlands", "Westbound Wetlands")
    public val SOUTH_WETLANDS = register("south_wetlands", "South Wetlands")
    public val MURKWATER_LOCH = register("murkwater_loch", "Murkwater Loch")
    public val WYRMGROVE_TOMB = register("wyrmgrove_tomb", "Wyrmgrove Tomb")
    public val EVERGREEN_PLATEAU = register("evergreen_plateau", "Evergreen Plateau")
    public val MURKWATER_OUTPOST = register("murkwater_outpost", "Murkwater Outpost")
    public val MURKWATER_DEPTHS = register("murkwater_depths", "Murkwater Depths")
    public val ANCIENT_RUINS = register("ancient_ruins", "Ancient Ruins")
    public val MURKWATER_SHALLOWS = register("murkwater_shallows", "Murkwater Shallows")
    public val DIVE_EMBER_PASS = register("dive_ember_pass", "Dive-Ember Pass")
    public val STRIDE_EMBER_FISSURE = register("stride_ember_fissure", "Stride-Ember Fissure")
    public val SIDE_EMBER_WAY = register("side_ember_way", "Side-Ember Way")
    public val REEFGUARD_DEPTHS = register("reefguard_depths", "Reefguard Depths")
    public val REEFGUARD_PASS = register("reefguard_pass", "Reefguard Pass")
    public val DROWNED_RELIQUARY = register("drowned_reliquary", "Drowned Reliquary")
    public val BUBBLEBOOST_COLUMN = register("bubbleboost_column", "Bubbleboost Column")
    public val KELPWOVEN_TUNNELS = register("kelpwoven_tunnels", "Kelpwoven Tunnels")
    public val RED_HOUSE = register("red_house", "Red House")
    public val TOMB_FLOODWAY = register("tomb_floodway", "Tomb Floodway")
    public val DRIPTOAD_DELVE = register("driptoad_delve", "Driptoad Delve")
    public val DRIPTOAD_PASS = register("driptoad_pass", "Driptoad Pass")
    public val TANGLEBURG_BANK = register("tangleburg_bank", "Tangleburg Bank")
    public val FUSION_HOUSE = register("fusion_house", "Fusion House")
    public val SWAMP_CUT_INC = register("swamp_cut_inc", "SwampCut Inc.")
    public val TANGLEBURG_LIBRARY = register("tangleburg_library", "Tangleburg Library")
    public val FOREST_TEMPLE = register("forest_temple", "Forest Temple")
    public val TRANQUILITY_SANCTUM = register("tranquility_sanctum", "Tranquility Sanctum")
    public val TRANQUIL_PASS = register("tranquil_pass", "Tranquil Pass")

    // Dwarven Mines
    public val THE_LIFT = register("the_lift", "The Lift")
    public val DWARVEN_VILLAGE = register("dwarven_village", "Dwarven Village")
    public val DWARVEN_MINES = register("dwarven_mines", "Dwarven Mines")
    public val LAVA_SPRINGS = register("lava_springs", "Lava Springs")
    public val PALACE_BRIDGE = register("palace_bridge", "Palace Bridge")
    public val ROYAL_PALACE = register("royal_palace", "Royal Palace")
    public val GRAND_LIBRARY = register("grand_library", "Grand Library")
    public val ROYAL_QUARTERS = register("royal_quarters", "Royal Quarters")
    public val BARRACKS_OF_HEROES = register("barracks_of_heroes", "Barracks of Heroes")
    public val HANGING_COURT = register("hanging_court", "Hanging Court")
    public val GREAT_ICE_WALL = register("great_ice_wall", "Great Ice Wall")
    public val ARISTOCRAT_PASSAGE = register("aristocrat_passage", "Aristocrat Passage")
    public val ROYAL_MINES = register("royal_mines", "Royal Mines")
    public val THE_MIST = register("the_mist", "The Mist")
    public val DIVANS_GATEWAY = register("divans_gateway", "Divan's Gateway")
    public val CLIFFSIDE_VEINS = register("cliffside_veins", "Cliffside Veins")
    public val FORGE_BASIN = register("forge_basin", "Forge Basin")
    public val THE_FORGE = register("the_forge", "The Forge")
    public val RAMPARTS_QUARRY = register("ramparts_quarry", "Rampart's Quarry")
    public val FAR_RESERVE = register("far_reserve", "Far Reserve")
    public val UPPER_MINES = register("upper_mines", "Upper Mines")
    public val ABANDONED_QUARRY = register("abandoned_quarry", "Abandoned Quarry")

    // Torrhus Canyon
    public val SAFARI_ZONE_ENTRANCE = register("safari_zone_entrance", "Safari Zone Entrance")
    public val TORRHUS_CANYON = register("torrhus_canyon", "Torrhus Canyon")
    public val TORRHUS_HEIGHTS = register("torrhus_heights", "Torrhus Heights")
    public val TORRHUS_SPRINGS = register("torrhus_springs", "Torrhus Springs")
    public val SPRING_SHALLOWS = register("spring_shallows", "Spring Shallows")
    public val SPRING_DEPTHS = register("spring_depths", "Spring Depths")
    public val SPRING_PATH = register("spring_path", "Spring Path")
    public val HOTSPOT_HAVEN = register("hotspot_haven", "Hotspot Haven")
    public val ANTS_CAVE = register("ants_cave", "Ant's Cave")
    public val DESERT_TEMPLE = register("desert_temple", "Desert Temple")
    public val MIRIAS_HUT = register("mirias_hut", "Miria's Hut")

    // Safari
    public val SAFARI_ZONE = register("safari_zone", "Safari Zone")

}
