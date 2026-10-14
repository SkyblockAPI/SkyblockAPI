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
    public companion object {
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

        public val NECKLACE: SkyBlockCategory = create("necklace")
        public val DUNGEON_NECKLACE: SkyBlockCategory = create("dungeon necklace")
        public val CLOAK: SkyBlockCategory = create("cloak")
        public val DUNGEON_CLOAK: SkyBlockCategory = create("dungeon cloak")
        public val BELT: SkyBlockCategory = create("belt")
        public val DUNGEON_BELT: SkyBlockCategory = create("dungeon belt")
        public val GLOVES: SkyBlockCategory = create("gloves")
        public val DUNGEON_GLOVES: SkyBlockCategory = create("dungeon gloves")
        public val BRACELET: SkyBlockCategory = create("bracelet")
        public val DUNGEON_BRACELET: SkyBlockCategory = create("dungeon bracelet")
        public val ARROW: SkyBlockCategory = create("arrow")
        public val ACCESSORY: SkyBlockCategory = create("accessory")
        public val DUNGEON_ACCESSORY: SkyBlockCategory = create("dungeon accessory")
        public val HATCESSORY: SkyBlockCategory = create("hatcessory")

        public val SWORD: SkyBlockCategory = create("sword")
        public val DUNGEON_SWORD: SkyBlockCategory = create("dungeon sword")
        public val LONGSWORD: SkyBlockCategory = create("longsword")
        public val DUNGEON_LONGSWORD: SkyBlockCategory = create("dungeon longsword")
        public val BOW: SkyBlockCategory = create("bow")
        public val DUNGEON_BOW: SkyBlockCategory = create("dungeon bow")
        public val WAND: SkyBlockCategory = create("wand")
        public val DUNGEON_WAND: SkyBlockCategory = create("dungeon wand")
        public val AXE: SkyBlockCategory = create("axe")
        public val GAUNTLET: SkyBlockCategory = create("gauntlet")
        public val PICKAXE: SkyBlockCategory = create("pickaxe")
        public val DUNGEON_PICKAXE: SkyBlockCategory = create("dungeon pickaxe")
        public val SHOVEL: SkyBlockCategory = create("shovel")
        public val DRILL: SkyBlockCategory = create("drill")
        public val SHEARS: SkyBlockCategory = create("shears")

        public val HELMET: SkyBlockCategory = create("helmet")
        public val DUNGEON_HELMET: SkyBlockCategory = create("dungeon helmet")
        public val CHESTPLATE: SkyBlockCategory = create("chestplate")
        public val DUNGEON_CHESTPLATE: SkyBlockCategory = create("dungeon chestplate")
        public val LEGGINGS: SkyBlockCategory = create("leggings")
        public val DUNGEON_LEGGINGS: SkyBlockCategory = create("dungeon leggings")
        public val BOOTS: SkyBlockCategory = create("boots")
        public val DUNGEON_BOOTS: SkyBlockCategory = create("dungeon boots")

        public val FISHING_ROD: SkyBlockCategory = create("fishing rod")
        public val ROD_PART: SkyBlockCategory = create("rod part") // in the api its called "FISHING_ROD_PART"
        public val BAIT: SkyBlockCategory = create("bait")

        //? < 26.2
        //@Deprecated("Trophy Fish dont exist anymore, theyre just Trophy", ReplaceWith("Trophy")) public val TROPHY_FISH: SkyBlockCategory = create("trophy fish")
        public val FISHING_NET: SkyBlockCategory = create("fishing net")

        public val DEPLOYABLE: SkyBlockCategory = create("deployable")
        public val VACUUM: SkyBlockCategory = create("vacuum")
        public val ABIPHONE: SkyBlockCategory = create("abiphone")
        public val CARNIVAL_MASK: SkyBlockCategory = create("carnival mask")
        public val POWER_STONE: SkyBlockCategory = create("power stone")
        public val TRAVEL_SCROLL: SkyBlockCategory = create("travel scroll")
        public val REFORGE_STONE: SkyBlockCategory = create("reforge stone")
        public val PET: SkyBlockCategory = create("pet")
        public val ARROW_POISON: SkyBlockCategory = create("arrow poison")
        public val PET_ITEM: SkyBlockCategory = create("pet item")
        public val ENCHANTED_BOOK: SkyBlockCategory = create("enchanted book")
        public val POTION: SkyBlockCategory = create("potion")
        public val RIFT_TIMECHARM: SkyBlockCategory = create("rift timecharm")
        public val COSMETIC: SkyBlockCategory = create("cosmetic")
        public val MEMENTO: SkyBlockCategory = create("memento")
        public val PORTAL: SkyBlockCategory = create("portal")
        public val SACK: SkyBlockCategory = create("sack")
        public val CHISEL: SkyBlockCategory = create("chisel")
        public val DYE: SkyBlockCategory = create("dye")
        public val ORE: SkyBlockCategory = create("ore")
        public val BLOCK: SkyBlockCategory = create("block")
        public val DWARVEN_METAL: SkyBlockCategory = create("dwarven metal")
        public val GEMSTONE: SkyBlockCategory = create("gemstone")
        public val LASSO: SkyBlockCategory = create("lasso")
        public val SALT: SkyBlockCategory = create("salt")
        public val TRAP: SkyBlockCategory = create("trap")
        public val BOOSTER: SkyBlockCategory = create("booster")

        public val WATER_SHARD: SkyBlockCategory = create("water shard")
        public val FOREST_SHARD: SkyBlockCategory = create("forest shard")
        public val COMBAT_SHARD: SkyBlockCategory = create("combat shard")
        public val MINING_SHARD: SkyBlockCategory = create("mining shard")

        public val GARDEN_CHIP: SkyBlockCategory = create("garden chip")
        public val MUTATION: SkyBlockCategory = create("mutation")
        public val WATERING_CAN: SkyBlockCategory = create("watering can")
        public val FARMING_TOOL: SkyBlockCategory = create("farming tool")
        public val TROPHY: SkyBlockCategory = create("trophy")
        public val ABILITY_SCROLL: SkyBlockCategory = create("ability scroll")
        public val CAPSULE: SkyBlockCategory = create("capsule")
        public val RABBIT: SkyBlockCategory = create("rabbit") // chocolate factory
        public val DUNGEON_PASS: SkyBlockCategory = create("dungeon pass") // seems to be admin only
    }
}
