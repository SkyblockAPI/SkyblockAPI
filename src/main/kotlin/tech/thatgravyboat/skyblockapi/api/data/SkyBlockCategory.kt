package tech.thatgravyboat.skyblockapi.api.data

public class SkyBlockCategory private constructor(
    public val name: String,
    public val isDungeon: Boolean = false
) {

    public fun equals(other: SkyBlockCategory, ignoreDungeon: Boolean = false): Boolean =
        if (!ignoreDungeon) this === other else this.name == other.name

    public fun equalsAny(vararg others: SkyBlockCategory, ignoreDungeon: Boolean = false): Boolean =
        others.any { equals(it, ignoreDungeon) }

    override fun toString(): String = if (isDungeon) "dungeon $name" else name

    @Suppress("unused")
    companion object {
        private val registeredCategories = mutableMapOf<String, SkyBlockCategory>()
        public val categories: Collection<SkyBlockCategory>
            get() = registeredCategories.values

        public fun create(string: String): SkyBlockCategory {
            val formatted = string.lowercase()
            return registeredCategories.getOrPut(formatted) {
                if (formatted.startsWith("dungeon", true)) {
                    SkyBlockCategory(formatted.removePrefix("dungeon").trim(), true)
                } else {
                    SkyBlockCategory(formatted.trim())
                }
            }
        }

        public val NECKLACE = create("necklace")
        public val DUNGEON_NECKLACE = create("dungeon necklace")
        public val CLOAK = create("cloak")
        public val DUNGEON_CLOAK = create("dungeon cloak")
        public val BELT = create("belt")
        public val DUNGEON_BELT = create("dungeon belt")
        public val GLOVES = create("gloves")
        public val DUNGEON_GLOVES = create("dungeon gloves")
        public val BRACELET = create("bracelet")
        public val DUNGEON_BRACELET = create("dungeon bracelet")
        public val ARROW = create("arrow")
        public val ACCESSORY = create("accessory")
        public val DUNGEON_ACCESSORY = create("dungeon accessory")
        public val HATCESSORY = create("hatcessory")

        public val SWORD = create("sword")
        public val DUNGEON_SWORD = create("dungeon sword")
        public val LONGSWORD = create("longsword")
        public val DUNGEON_LONGSWORD = create("dungeon longsword")
        public val BOW = create("bow")
        public val DUNGEON_BOW = create("dungeon bow")
        public val WAND = create("wand")
        public val DUNGEON_WAND = create("dungeon wand")
        public val AXE = create("axe")
        public val GAUNTLET = create("gauntlet")
        public val PICKAXE = create("pickaxe")
        public val DUNGEON_PICKAXE = create("dungeon pickaxe")
        public val SHOVEL = create("shovel")
        public val DRILL = create("drill")
        public val SHEARS = create("shears")

        public val HELMET = create("helmet")
        public val DUNGEON_HELMET = create("dungeon helmet")
        public val CHESTPLATE = create("chestplate")
        public val DUNGEON_CHESTPLATE = create("dungeon chestplate")
        public val LEGGINGS = create("leggings")
        public val DUNGEON_LEGGINGS = create("dungeon leggings")
        public val BOOTS = create("boots")
        public val DUNGEON_BOOTS = create("dungeon boots")

        public val FISHING_ROD = create("fishing rod")
        public val ROD_PART = create("rod part") // in the api its called "FISHING_ROD_PART"
        public val BAIT = create("bait")

        //? < 26.2
        //@Deprecated("Trophy Fish dont exist anymore, theyre just Trophy", ReplaceWith("Trophy")) val TROPHY_FISH = create("trophy fish")
        public val FISHING_NET = create("fishing net")

        public val DEPLOYABLE = create("deployable")
        public val VACUUM = create("vacuum")
        public val ABIPHONE = create("abiphone")
        public val CARNIVAL_MASK = create("carnival mask")
        public val POWER_STONE = create("power stone")
        public val TRAVEL_SCROLL = create("travel scroll")
        public val REFORGE_STONE = create("reforge stone")
        public val PET = create("pet")
        public val ARROW_POISON = create("arrow poison")
        public val PET_ITEM = create("pet item")
        public val ENCHANTED_BOOK = create("enchanted book")
        public val POTION = create("potion")
        public val RIFT_TIMECHARM = create("rift timecharm")
        public val COSMETIC = create("cosmetic")
        public val MEMENTO = create("memento")
        public val PORTAL = create("portal")
        public val SACK = create("sack")
        public val CHISEL = create("chisel")
        public val DYE = create("dye")
        public val ORE = create("ore")
        public val BLOCK = create("block")
        public val DWARVEN_METAL = create("dwarven metal")
        public val GEMSTONE = create("gemstone")
        public val LASSO = create("lasso")
        public val SALT = create("salt")
        public val TRAP = create("trap")
        public val BOOSTER = create("booster")

        public val WATER_SHARD = create("water shard")
        public val FOREST_SHARD = create("forest shard")
        public val COMBAT_SHARD = create("combat shard")
        public val MINING_SHARD = create("mining shard")

        public val GARDEN_CHIP = create("garden chip")
        public val MUTATION = create("mutation")
        public val WATERING_CAN = create("watering can")
        public val FARMING_TOOL = create("farming tool")
        public val TROPHY = create("trophy")
        public val ABILITY_SCROLL = create("ability scroll")
        public val CAPSULE = create("capsule")
        public val RABBIT = create("rabbit") // chocolate factory
        public val DUNGEON_PASS = create("dungeon pass") // seems to be admin only
    }
}
