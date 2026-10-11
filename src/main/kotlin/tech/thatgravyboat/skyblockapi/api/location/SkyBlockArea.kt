package tech.thatgravyboat.skyblockapi.api.location

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
public data class SkyBlockArea(val name: String) {
    public fun inArea(): Boolean = LocationAPI.area == this

    public companion object {

        public fun inAnyArea(vararg areas: SkyBlockArea): Boolean = LocationAPI.area in areas
        public fun inAnyArea(areas: Collection<SkyBlockArea>): Boolean = LocationAPI.area in areas
    }
}

@Suppress("unused")
public object SkyBlockAreas {

    internal val registeredAreas = mutableMapOf<String, SkyBlockArea>()

    private fun register(key: String, name: String) = registeredAreas.getOrPut(key) { SkyBlockArea(name) }

    public val NONE: SkyBlockArea = register("none", "None")
    public val PRIVATE_ISLAND: SkyBlockArea = register("private_island", "Your Island")
    public val GARDEN: SkyBlockArea = register("garden", "The Garden")

    // Hub
    public val VILLAGE: SkyBlockArea = register("village", "Village")
    public val FOREST: SkyBlockArea = register("forest", "Forest")
    public val PET_CARE: SkyBlockArea = register("pet_care", "Pet Care")
    public val FARM: SkyBlockArea = register("farm", "Farm")
    public val ARTISTS_ABODE: SkyBlockArea = register("artists_abode", "Artist's Abode")
    public val COLOSSEUM: SkyBlockArea = register("colosseum", "Colosseum")
    public val FASHION_SHOP: SkyBlockArea = register("fashion_shop", "Fashion Shop")
    public val FLOWER_HOUSE: SkyBlockArea = register("flower_house", "Flower House")
    public val CANVAS_ROOM: SkyBlockArea = register("canvas_room", "Canvas Room")
    public val MOUNTAIN: SkyBlockArea = register("mountain", "Mountain")
    public val BANK: SkyBlockArea = register("bank", "Bank")
    public val AUCTION_HOUSE: SkyBlockArea = register("auction_house", "Auction House")
    public val SHENS_AUCTION: SkyBlockArea = register("shens_auction", "Shen's Auction")
    public val COMMUNITY_CENTER: SkyBlockArea = register("community_center", "Community Center")
    public val ELECTION_ROOM: SkyBlockArea = register("election_room", "Election Room")
    public val FARMHOUSE: SkyBlockArea = register("farmhouse", "Farmhouse")
    public val WEAPONSMITH: SkyBlockArea = register("weaponsmith", "Weaponsmith")
    public val BLACKSMITH: SkyBlockArea = register("blacksmith", "Blacksmith")
    public val ARCHERY_RANGE: SkyBlockArea = register("archery_range", "Archery Range")
    public val LIBRARY: SkyBlockArea = register("library", "Library")
    public val HEXATORUM: SkyBlockArea = register("hexatorium", "Hexatorium")
    public val TRADE_CENTER: SkyBlockArea = register("trade_center", "Trade Center")
    public val BUILDERS_HOUSE: SkyBlockArea = register("builders_house", "Builder's House")
    public val TAVERN: SkyBlockArea = register("tavern", "Tavern")
    public val GRAVEYARD: SkyBlockArea = register("graveyard", "Graveyard")
    public val COAL_MINE: SkyBlockArea = register("coal_mine", "Coal Mine")
    public val BAZAAR_ALLEY: SkyBlockArea = register("bazaar_alley", "Bazaar Alley")
    public val WILDERNESS: SkyBlockArea = register("wilderness", "Wilderness")
    public val FISHING_OUTPOST: SkyBlockArea = register("fishing_outpost", "Fishing Outpost")
    public val FISHERMANS_HUT: SkyBlockArea = register("fishermans_hut", "Fisherman's Hut")
    public val UNINCORPORATED: SkyBlockArea = register("unincorporated", "Unincorporated")
    public val WIZARD_TOWER: SkyBlockArea = register("wizard_tower", "Wizard Tower")
    public val RUINS: SkyBlockArea = register("ruins", "Ruins")

    // Rift
    public val WYLD_WOODS: SkyBlockArea = register("wyld_woods", "Wyld Woods")
    public val THE_BASTION: SkyBlockArea = register("the_bastion", "The Bastion")
    public val BROKEN_CAGE: SkyBlockArea = register("broken_cage", "Broken Cage")
    public val SHIFTED_TAVERN: SkyBlockArea = register("shifted_tavern", "Shifted Tavern")
    public val BLACK_LAGOON: SkyBlockArea = register("black_lagoon", "Black Lagoon")
    public val LAGOON_CAVE: SkyBlockArea = register("lagoon_cave", "Lagoon Cave")
    public val OTHERSIDE: SkyBlockArea = register("otherside", "Otherside")
    public val LEECHES_LAIR: SkyBlockArea = register("leeches_lair", "Leeches Lair")
    public val LAGOON_HUT: SkyBlockArea = register("lagoon_hut", "Lagoon Hut")
    public val AROUND_COLOSSEUM: SkyBlockArea = register("around_colosseum", "Around Colosseum")
    public val WEST_VILLAGE: SkyBlockArea = register("west_village", "West Village")
    public val DOPLHIN_TRAINER: SkyBlockArea = register("dolphin_trainer", "Dolphin Trainer")
    public val INFESTED_HOUSE: SkyBlockArea = register("infested_house", "Infested House")
    public val DREADFARM: SkyBlockArea = register("dreadfarm", "Dreadfarm")
    public val MIRRORVERSE: SkyBlockArea = register("mirrorverse", "Mirrorverse")
    public val CAKE_HOUSE: SkyBlockArea = register("cake_house", "Cake House")
    public val VILLAGE_PLAZA: SkyBlockArea = register("village_plaza", "Village Plaza")
    public val MURDER_HOUSE: SkyBlockArea = register("murder_house", "Murder House")
    public val TAYLORS: SkyBlockArea = register("taylors", "Taylor's")
    public val HALF_EATEN_CAVE: SkyBlockArea = register("half_eaten_cave", "Half-Eaten Cave")
    public val BOOK_IN_A_BOOK: SkyBlockArea = register("book_in_a_book", "Book in a Book")
    public val EMPTY_BANK: SkyBlockArea = register("empty_bank", "Empty Bank")
    public val BARRIER_STREET: SkyBlockArea = register("barrier_street", "Barrier Street")
    public val BARRY_CENTER: SkyBlockArea = register("barry_center", "Barry Center")
    public val BARRY_HQ: SkyBlockArea = register("barry_hq", "Barry HQ")
    public val RIFT_GALLERY: SkyBlockArea = register("rift_gallery", "Rift Gallery")
    public val RIFT_GALLERY_ENTRANCE: SkyBlockArea = register("rift_gallery_entrance", "Rift Gallery Entrance")
    public val THE_MOUNTAINTOP: SkyBlockArea = register("the_mountaintop", "The Mountaintop")
    public val WIZARDMAN_BUREAU: SkyBlockArea = register("wizardman_bureau", "Wizardman Bureau")
    public val THE_VENTS: SkyBlockArea = register("the_vents", "The Vents")
    public val CEREBRAL_CITADEL: SkyBlockArea = register("cerebral_citadel", "Cerebral Citadel")
    public val WALK_OF_FAME: SkyBlockArea = register("walk_of_fame", "Walk of Fame")
    public val TRIAL_GROUNDS: SkyBlockArea = register("trial_grounds", "Trial Grounds")
    public val CONTINUUM: SkyBlockArea = register("continuum", "Continuum")
    public val TIME_CHAMBER: SkyBlockArea = register("time_chamber", "Time Chamber")

    // Rift-Slayer
    public val PHOTON_PATHWAY: SkyBlockArea = register("photon_pathway", "Photon Pathway")
    public val STILLGORE_CHATEAU: SkyBlockArea = register("stillgore_chateau", "Stillgore Château")
    public val OUBLIETTE: SkyBlockArea = register("oubliette", "Oubliette")
    public val FAIRYLOSOPHER_TOWER: SkyBlockArea = register("fairylosopher_tower", "Fairylosopher Tower")

    // Dwarves
    public val BASECAMP: SkyBlockArea = register("basecamp", "Dwarven Base Camp")
    public val FOSSIL_RESEARCH: SkyBlockArea = register("fossil_research", "Fossil Research Center")
    public val GLACITE_TUNNELS: SkyBlockArea = register("glacite_tunnels", "Glacite Tunnels")
    public val GREAT_LAKE: SkyBlockArea = register("great_lake", "Great Glacite Lake")

    // Crimson
    public val DOJO: SkyBlockArea = register("dojo", "Dojo")
    public val DOJO_ARENA: SkyBlockArea = register("dojo_arena", "Dojo Arena")
    public val MAGMA_CHAMBER: SkyBlockArea = register("magma_chamber", "Magma Chamber")
    public val CRIMSON_ISLE: SkyBlockArea = register("crimson_isle", "Crimson Isle")
    public val CRIMSON_FIELDS: SkyBlockArea = register("crimson_fields", "Crimson Fields")
    public val BURNING_DESERT: SkyBlockArea = register("burning_desert", "Burning Desert")
    public val DRAGONTAIL: SkyBlockArea = register("dragontail", "Dragontail")
    public val DRAGONTAIL_BLACKSMITH: SkyBlockArea = register("dragontail_blacksmith", "Dragontail Blacksmith")
    public val DRAGONTAIL_BANK: SkyBlockArea = register("dragontail_bank", "Dragontail Bank")
    public val DRAGONTAIL_TOWNSQUARE: SkyBlockArea = register("dragontail_townsquare", "Dragontail Townsquare")
    public val DRAGONTAIL_AUCTION_HOUS: SkyBlockArea = register("dragontail_auction_hous", "Dragontail Auction Hous")
    public val MINION_SHOP: SkyBlockArea = register("minion_shop", "Minion Shop")
    public val THE_DUKEDOM: SkyBlockArea = register("the_dukedom", "The Dukedom")
    public val BLAZING_VOLCANO: SkyBlockArea = register("blazing_volcano", "Blazing Volcano")
    public val ODGER_HUT: SkyBlockArea = register("odger_hut", "Odger's Hut")
    public val THE_WASTELAND: SkyBlockArea = register("the_wasteland", "The Wasteland")
    public val FORGOTTEN_SKULL: SkyBlockArea = register("forgotten_skull", "Forgotten Skull")
    public val SCARLETON: SkyBlockArea = register("scarleton", "Scarleton")
    public val COURTYARD: SkyBlockArea = register("courtyard", "Courtyard")
    public val SCARLETON_BANK: SkyBlockArea = register("scarleton_bank", "Scarleton Bank")
    public val SCARLETON_PLAZA: SkyBlockArea = register("scarleton_plaza", "Scarleton Plaza")
    public val SCARLETON_AUCTION_HOUSE: SkyBlockArea = register("scarleton_auction_house", "Scarleton Auction House")
    public val SCARLETON_BAZAAR: SkyBlockArea = register("scarleton_bazaar", "Scarleton Bazaar")
    public val SCARLETON_MINION_SHOP: SkyBlockArea = register("scarleton_minion_shop", "Scarleton Minion Shop")
    public val SCARLETON_BLACKSMITH: SkyBlockArea = register("scarleton_blacksmith", "Scarleton Blacksmith")
    public val CATHEDRAL: SkyBlockArea = register("cathedral", "Cathedral")
    public val MYSTIC_MARSH: SkyBlockArea = register("mystic_marsh", "Mystic Marsh")
    public val MATRIARCH_LAIR: SkyBlockArea = register("matriarch_lair", "Matriarch's Lair")
    public val BELLY_OF_THE_BEAST: SkyBlockArea = register("belly_of_the_beast", "Belly of the Beast")
    public val SMOLDERING_TOMB: SkyBlockArea = register("smoldering_tomb", "Smoldering Tomb")


    // Jerry
    public val GLACIAL_CAVE: SkyBlockArea = register("glacial_cave", "Glacial Cave")
    public val MOUNT_JERRY: SkyBlockArea = register("mount_jerry", "Mount Jerry")
    public val HOT_SPRINGS: SkyBlockArea = register("hot_springs", "Hot Springs")
    public val JERRY_POND: SkyBlockArea = register("jerry_pond", "Jerry Pond")
    public val REFLECTIVE_POND: SkyBlockArea = register("reflective_pond", "Reflective Pond")
    public val TERRYS_SHACK: SkyBlockArea = register("terrys_shack", "Terry's Shack")
    public val SUNKEN_JERRY_POND: SkyBlockArea = register("sunken_jerry_pond", "Sunken Jerry Pond")
    public val EINARYS_EMPORIUM: SkyBlockArea = register("einarys_emporioum", "Einary's Emporium")
    public val SHERRYS_SHOWROOM: SkyBlockArea = register("sherrys_showroom", "Sherry's Showroom")
    public val GARYS_SHACK: SkyBlockArea = register("garys_shack", "Gary's Shack")

    // Spider
    public val SPIDER_MOUND: SkyBlockArea = register("spider_mound", "Spider Mound")
    public val GRAVEL_MINES: SkyBlockArea = register("gravel_mines", "Gravel Mines")
    public val GRANDMAS_HOUSE: SkyBlockArea = register("grandmas_house", "Grandma's House")
    public val ARACHNES_BURROW: SkyBlockArea = register("arachnes_burrow", "Arachne's Burrow")
    public val ARACHNES_SANCTUARY: SkyBlockArea = register("arachnes_sanctuary", "Arachne's Sanctuary")
    public val ARCHAEOLOGISTS_CAMP: SkyBlockArea = register("archaeologists_camp", "Archaeologist's Camp")

    // End
    public val DRAGONS_NEST: SkyBlockArea = register("dragons_nest", "Dragon's Nest")
    public val VOID_SEPULTURE: SkyBlockArea = register("void_sepulture", "Void Sepulture")
    public val VOID_SLATE: SkyBlockArea = register("void_slate", "Void Slate")
    public val ZEALOT_BRUISER_HIDEOUT: SkyBlockArea = register("zealot_bruiser_hideout", "Zealot Bruiser Hideout")

    // Farming Islands
    public val THE_BARN: SkyBlockArea = register("the_barn", "The Barn")
    public val MUSHROOM_DESERT: SkyBlockArea = register("mushroom_desert", "Mushroom Desert")
    public val WINDMILL: SkyBlockArea = register("windmill", "Windmill")
    public val DESERT_SETTLEMENT: SkyBlockArea = register("desert_settlement", "Desert Settlement")
    public val GLOWING_MUSHROOM_CAVE: SkyBlockArea = register("glowing_mushroom_cave", "Glowing Mushroom Cave")
    public val JAKES_HOUSE: SkyBlockArea = register("jakes_house", "Jake's House")
    public val MUSHROOM_GORGE: SkyBlockArea = register("mushroom_gorge", "Mushroom Gorge")
    public val OASIS: SkyBlockArea = register("oasis", "Oasis")
    public val OVERGROWN_MUSHROOM_CAVE: SkyBlockArea = register("overgrown_mushroom_cave", "Overgrown Mushroom Cave")
    public val SHEPHERDS_KEEP: SkyBlockArea = register("shepherds_keep", "Shepherd's Keep")
    public val TRAPPERS_DEN: SkyBlockArea = register("trappers_den", "Trapper's Den")
    public val TREASURE_HUNTER_CAMP: SkyBlockArea = register("treasure_hunter_camp", "Treasure Hunter Camp")

    // Park
    public val BIRCH_PARK: SkyBlockArea = register("birch_park", "Birch Park")
    public val HOWLING_CAVE: SkyBlockArea = register("howling_cave", "Howling Cave")
    public val SOUL_CAVE: SkyBlockArea = register("soul_cave", "Soul Cave")
    public val SPIRIT_CAVE: SkyBlockArea = register("spirit_cave", "Spirit Cave")
    public val SPRUCE_WOODS: SkyBlockArea = register("spruce_woods", "Spruce Woods")
    public val LONELY_ISLAND: SkyBlockArea = register("lonely_island", "Lonely Island")
    public val VIKING_LONGHOUSE: SkyBlockArea = register("viking_longhouse", "Viking Longhouse")
    public val DARK_THICKET: SkyBlockArea = register("dark_thicket", "Dark Thicket")
    public val SAVANNA_WOODLAND: SkyBlockArea = register("savanna_woodland", "Savanna Woodland")
    public val MELODYS_PLATEAU: SkyBlockArea = register("melodys_plateau", "Melody's Plateau")
    public val JUNGLE_ISLAND: SkyBlockArea = register("jungle_island", "Jungle Island")

    // Deep Caverns
    public val DEEP_CAVERNS: SkyBlockArea = register("deep_caverns", "Deep Caverns")
    public val GUNPOWDER_MINES: SkyBlockArea = register("gunpowder_mines", "Gunpowder Mines")
    public val LAPIS_QUARRY: SkyBlockArea = register("lapis_quarry", "Lapis Quarry")
    public val PIGMENS_DEN: SkyBlockArea = register("pigmens_den", "Pigmen's Den")
    public val SLIMEHILL: SkyBlockArea = register("slimehill", "Slimehill")
    public val DIAMOND_RESERVE: SkyBlockArea = register("diamond_reserve", "Diamond Reserve")
    public val OBSIDIAN_SANCTUARY: SkyBlockArea = register("obsidian_sanctuary", "Obsidian Sanctuary")

    // Crystal Hollows
    public val CRYSTAL_NUCLEUS: SkyBlockArea = register("crystal_nucleus", "Crystal Nucleus")
    public val GOBLIN_HOLDOUT: SkyBlockArea = register("goblin_holdout", "Goblin Holdout")
    public val GOBLIN_QUEENS_DEN: SkyBlockArea = register("goblin_queens_den", "Goblin Queen's Den")
    public val JUNGLE: SkyBlockArea = register("jungle", "Jungle")
    public val JUNGLE_TEMPLE: SkyBlockArea = register("jungle_temple", "Jungle Temple")
    public val PRECURSOR_REMNANTS: SkyBlockArea = register("precursor_remnants", "Precursor Remnants")
    public val LOST_PRECURSOR_CITY: SkyBlockArea = register("lost_precursor_city", "Lost Precursor City")
    public val MITHRIL_DEPOSITS: SkyBlockArea = register("mithril_deposits", "Mithril Deposits")
    public val DRAGONS_LAIR: SkyBlockArea = register("dragons_lair", "Dragon's Lair") // also in galatea
    public val MINES_OF_DIVAN: SkyBlockArea = register("mines_of_divan", "Mines of Divan")
    public val MAGMA_FIELDS: SkyBlockArea = register("magma_fields", "Magma Fields")
    public val KHAZAD_DUM: SkyBlockArea = register("khazad_dum", "Khazad-dûm")
    public val FAIRY_GROTTO: SkyBlockArea = register("fairy_grotto", "Fairy Grotto")

    // Backwater Bayou
    public val BACKWATER_BAYOU: SkyBlockArea = register("backwater_bayou", "Backwater Bayou") // The full island uses this

    // Lotus Atoll
    public val LOTUS_ATOLL: SkyBlockArea = register("lotus_atoll", "Lotus Atoll")
    public val LOTUS_EATERS_CAVE: SkyBlockArea = register("lotus_eaters_cave", "Lotus Eater's Cave")
    public val LOTUS_HIGHLANDS: SkyBlockArea = register("lotus_highlands", "Lotus Highlands")

    // Galatea
    public val TANGLEBURG_PATH: SkyBlockArea = register("tangleburg_path", "Tangleburg's Path")
    public val TANGLEBURG: SkyBlockArea = register("tangleburg", "Tangleburg")
    public val NORTH_REACHES: SkyBlockArea = register("north_reaches", "North Reaches")
    public val WEST_REACHES: SkyBlockArea = register("west_reaches", "West Reaches")
    public val SOUTH_REACHES: SkyBlockArea = register("south_reaches", "South Reaches")
    public val MOONGLADE_MARSH: SkyBlockArea = register("moonglade_marsh", "Moonglade Marsh")
    public val MOONGLADE_EDGE: SkyBlockArea = register("moonglade_edge", "Moonglade's Edge")
    public val VERDANT_SUMMIT: SkyBlockArea = register("verdant_summit", "Verdant Summit")
    public val NORTH_WETLANDS: SkyBlockArea = register("north_wetlands", "North Wetlands")
    public val WESTBOUND_WETLANDS: SkyBlockArea = register("westbound_wetlands", "Westbound Wetlands")
    public val SOUTH_WETLANDS: SkyBlockArea = register("south_wetlands", "South Wetlands")
    public val MURKWATER_LOCH: SkyBlockArea = register("murkwater_loch", "Murkwater Loch")
    public val WYRMGROVE_TOMB: SkyBlockArea = register("wyrmgrove_tomb", "Wyrmgrove Tomb")
    public val EVERGREEN_PLATEAU: SkyBlockArea = register("evergreen_plateau", "Evergreen Plateau")
    public val MURKWATER_OUTPOST: SkyBlockArea = register("murkwater_outpost", "Murkwater Outpost")
    public val MURKWATER_DEPTHS: SkyBlockArea = register("murkwater_depths", "Murkwater Depths")
    public val ANCIENT_RUINS: SkyBlockArea = register("ancient_ruins", "Ancient Ruins")
    public val MURKWATER_SHALLOWS: SkyBlockArea = register("murkwater_shallows", "Murkwater Shallows")
    public val DIVE_EMBER_PASS: SkyBlockArea = register("dive_ember_pass", "Dive-Ember Pass")
    public val STRIDE_EMBER_FISSURE: SkyBlockArea = register("stride_ember_fissure", "Stride-Ember Fissure")
    public val SIDE_EMBER_WAY: SkyBlockArea = register("side_ember_way", "Side-Ember Way")
    public val REEFGUARD_DEPTHS: SkyBlockArea = register("reefguard_depths", "Reefguard Depths")
    public val REEFGUARD_PASS: SkyBlockArea = register("reefguard_pass", "Reefguard Pass")
    public val DROWNED_RELIQUARY: SkyBlockArea = register("drowned_reliquary", "Drowned Reliquary")
    public val BUBBLEBOOST_COLUMN: SkyBlockArea = register("bubbleboost_column", "Bubbleboost Column")
    public val KELPWOVEN_TUNNELS: SkyBlockArea = register("kelpwoven_tunnels", "Kelpwoven Tunnels")
    public val RED_HOUSE: SkyBlockArea = register("red_house", "Red House")
    public val TOMB_FLOODWAY: SkyBlockArea = register("tomb_floodway", "Tomb Floodway")
    public val DRIPTOAD_DELVE: SkyBlockArea = register("driptoad_delve", "Driptoad Delve")
    public val DRIPTOAD_PASS: SkyBlockArea = register("driptoad_pass", "Driptoad Pass")
    public val TANGLEBURG_BANK: SkyBlockArea = register("tangleburg_bank", "Tangleburg Bank")
    public val FUSION_HOUSE: SkyBlockArea = register("fusion_house", "Fusion House")
    public val SWAMP_CUT_INC: SkyBlockArea = register("swamp_cut_inc", "SwampCut Inc.")
    public val TANGLEBURG_LIBRARY: SkyBlockArea = register("tangleburg_library", "Tangleburg Library")
    public val FOREST_TEMPLE: SkyBlockArea = register("forest_temple", "Forest Temple")
    public val TRANQUILITY_SANCTUM: SkyBlockArea = register("tranquility_sanctum", "Tranquility Sanctum")
    public val TRANQUIL_PASS: SkyBlockArea = register("tranquil_pass", "Tranquil Pass")

    // Dwarven Mines
    public val THE_LIFT: SkyBlockArea = register("the_lift", "The Lift")
    public val DWARVEN_VILLAGE: SkyBlockArea = register("dwarven_village", "Dwarven Village")
    public val DWARVEN_MINES: SkyBlockArea = register("dwarven_mines", "Dwarven Mines")
    public val LAVA_SPRINGS: SkyBlockArea = register("lava_springs", "Lava Springs")
    public val PALACE_BRIDGE: SkyBlockArea = register("palace_bridge", "Palace Bridge")
    public val ROYAL_PALACE: SkyBlockArea = register("royal_palace", "Royal Palace")
    public val GRAND_LIBRARY: SkyBlockArea = register("grand_library", "Grand Library")
    public val ROYAL_QUARTERS: SkyBlockArea = register("royal_quarters", "Royal Quarters")
    public val BARRACKS_OF_HEROES: SkyBlockArea = register("barracks_of_heroes", "Barracks of Heroes")
    public val HANGING_COURT: SkyBlockArea = register("hanging_court", "Hanging Court")
    public val GREAT_ICE_WALL: SkyBlockArea = register("great_ice_wall", "Great Ice Wall")
    public val ARISTOCRAT_PASSAGE: SkyBlockArea = register("aristocrat_passage", "Aristocrat Passage")
    public val ROYAL_MINES: SkyBlockArea = register("royal_mines", "Royal Mines")
    public val THE_MIST: SkyBlockArea = register("the_mist", "The Mist")
    public val DIVANS_GATEWAY: SkyBlockArea = register("divans_gateway", "Divan's Gateway")
    public val CLIFFSIDE_VEINS: SkyBlockArea = register("cliffside_veins", "Cliffside Veins")
    public val FORGE_BASIN: SkyBlockArea = register("forge_basin", "Forge Basin")
    public val THE_FORGE: SkyBlockArea = register("the_forge", "The Forge")
    public val RAMPARTS_QUARRY: SkyBlockArea = register("ramparts_quarry", "Rampart's Quarry")
    public val FAR_RESERVE: SkyBlockArea = register("far_reserve", "Far Reserve")
    public val UPPER_MINES: SkyBlockArea = register("upper_mines", "Upper Mines")
    public val ABANDONED_QUARRY: SkyBlockArea = register("abandoned_quarry", "Abandoned Quarry")

    // Torrhus Canyon
    public val SAFARI_ZONE_ENTRANCE: SkyBlockArea = register("safari_zone_entrance", "Safari Zone Entrance")
    public val TORRHUS_CANYON: SkyBlockArea = register("torrhus_canyon", "Torrhus Canyon")
    public val TORRHUS_HEIGHTS: SkyBlockArea = register("torrhus_heights", "Torrhus Heights")
    public val TORRHUS_SPRINGS: SkyBlockArea = register("torrhus_springs", "Torrhus Springs")
    public val SPRING_SHALLOWS: SkyBlockArea = register("spring_shallows", "Spring Shallows")
    public val SPRING_DEPTHS: SkyBlockArea = register("spring_depths", "Spring Depths")
    public val SPRING_PATH: SkyBlockArea = register("spring_path", "Spring Path")
    public val HOTSPOT_HAVEN: SkyBlockArea = register("hotspot_haven", "Hotspot Haven")
    public val ANTS_CAVE: SkyBlockArea = register("ants_cave", "Ant's Cave")
    public val DESERT_TEMPLE: SkyBlockArea = register("desert_temple", "Desert Temple")
    public val MIRIAS_HUT: SkyBlockArea = register("mirias_hut", "Miria's Hut")

    // Safari
    public val SAFARI_ZONE: SkyBlockArea = register("safari_zone", "Safari Zone")

}
