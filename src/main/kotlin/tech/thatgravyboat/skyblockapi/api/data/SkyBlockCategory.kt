package tech.thatgravyboat.skyblockapi.api.data

class SkyBlockCategory private constructor(
    val name: String,
    val isDungeon: Boolean = false
) {

    fun equals(other: SkyBlockCategory, ignoreDungeon: Boolean = false): Boolean =
        if (!ignoreDungeon) this === other else this.name == other.name

    fun equalsAny(vararg others: SkyBlockCategory, ignoreDungeon: Boolean = false): Boolean =
        others.any { equals(it, ignoreDungeon) }

    override fun toString(): String = if (isDungeon) "dungeon $name" else name

    @Suppress("unused")
    companion object {
        private val registeredCategories = mutableMapOf<String, SkyBlockCategory>()

        fun create(string: String): SkyBlockCategory {
            val formatted = string.lowercase()
            return registeredCategories.getOrPut(formatted) {
                if (formatted.startsWith("dungeon", true)) {
                    SkyBlockCategory(formatted.removePrefix("dungeon").trim(), true)
                } else {
                    SkyBlockCategory(formatted.trim())
                }
            }
        }

        val NECKLACE = create("necklace")
        val DUNGEON_NECKLACE = create("dungeon necklace")
        val CLOAK = create("cloak")
        val DUNGEON_CLOAK = create("dungeon cloak")
        val BELT = create("belt")
        val DUNGEON_BELT = create("dungeon belt")
        val GLOVES = create("gloves")
        val DUNGEON_GLOVES = create("dungeon gloves")
        val BRACELET = create("bracelet")
        val DUNGEON_BRACELET = create("dungeon bracelet")
        val ARROW = create("arrow")
        val ACCESSORY = create("accessory")
        val DUNGEON_ACCESSORY = create("dungeon accessory")
        val HATCESSORY = create("hatcessory")

        val SWORD = create("sword")
        val DUNGEON_SWORD = create("dungeon sword")
        val LONGSWORD = create("longsword")
        val DUNGEON_LONGSWORD = create("dungeon longsword")
        val BOW = create("bow")
        val DUNGEON_BOW = create("dungeon bow")
        val SHORT_BOW = create("short_bow")
        val DUNGEON_SHORT_BOW = create("dungeon short_bow")
        val WAND = create("wand")
        val DUNGEON_WAND = create("dungeon wand")
        val AXE = create("axe")
        val GAUNTLET = create("gauntlet")
        val PICKAXE = create("pickaxe")
        val DUNGEON_PICKAXE = create("dungeon pickaxe")
        val SHOVEL = create("shovel")
        val DRILL = create("drill")
        val SHEARS = create("shears")

        val HELMET = create("helmet")
        val DUNGEON_HELMET = create("dungeon helmet")
        val CHESTPLATE = create("chestplate")
        val DUNGEON_CHESTPLATE = create("dungeon chestplate")
        val LEGGINGS = create("leggings")
        val DUNGEON_LEGGINGS = create("dungeon leggings")
        val BOOTS = create("boots")
        val DUNGEON_BOOTS = create("dungeon boots")

        val FISHING_ROD = create("fishing rod")
        val ROD_PART = create("rod part")
        val FISHING_BAIT = create("fishing bait")
        val BAIT = create("bait")
        val TROPHY_FISH = create("trophy fish")
        val FISHING_NET = create("fishing net")

        val DEPLOYABLE = create("deployable")
        val VACUUM = create("vacuum")
        val ABIPHONE = create("abiphone")
        val CARNIVAL_MASK = create("carnival mask")
        val POWER_STONE = create("power stone")
        val TRAVEL_SCROLL = create("travel scroll")
        val REFORGE_STONE = create("reforge stone")
        val PET = create("pet")
        val ARROW_POISON = create("arrow poison")
        val PET_ITEM = create("pet item")
        val ENCHANTED_BOOK = create("enchanted book")
        val POTION = create("potion")
        val RIFT_TIMECHARM = create("rift timecharm")
        val COSMETIC = create("cosmetic")
        val MEMENTO = create("memento")
        val PORTAL = create("portal")
        val SACK = create("sack")
        val CHISEL = create("chisel")
        val DYE = create("dye")
        val ORE = create("ore")
        val BLOCK = create("block")
        val DWARVEN_METAL = create("dwarven metal")
        val GEMSTONE = create("gemstone")
        val LASSO = create("lasso")
        val SALT = create("salt")
        val TRAP = create("trap")
        val BOOSTER = create("booster")
        val WATER_SHARD = create("water shard")
        val FOREST_SHARD = create("forest shard")
        val COMBAT_SHARD = create("combat shard")
        val GARDEN_CHIP = create("garden chip")
        val MUTATION = create("mutation")
        val WATERING_CAN = create("watering can")
        val FARMING_TOOL = create("farming tool")
        val TROPHY = create("trophy")
    }
}
