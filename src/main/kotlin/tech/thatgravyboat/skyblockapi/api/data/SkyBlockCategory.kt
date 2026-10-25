package tech.thatgravyboat.skyblockapi.api.data

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.devutils.SkyBlockApiDevUtils.debugString
import tech.thatgravyboat.skyblockapi.utils.devutils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.pluralize
import tech.thatgravyboat.skyblockapi.utils.text.Text

public class SkyBlockCategory private constructor(
    public val name: String,
    public val modifier: Modifier? = null,
) {

    public val isDungeon: Boolean get() = modifier == Modifier.DUNGEON
    public val isSlayer: Boolean get() = modifier == Modifier.SLAYER

    public fun equals(other: SkyBlockCategory, ignoreModifiers: Boolean = false): Boolean =
        if (!ignoreModifiers) this === other else this.name == other.name

    public fun equalsAny(vararg others: SkyBlockCategory, ignoreModifiers: Boolean = false): Boolean =
        others.any { equals(it, ignoreModifiers) }

    override fun toString(): String = if (modifier != null) "$modifier $name" else name

    @Module
    public companion object {
        //region Private
        private val missingCategories = mutableSetOf<String>()
        private var finishedKnownCategories: Boolean = false
        private val debugToggle = debugToggle("unknown.skyblock_category", "Warns when detecting a new unknown skyblock category")

        private fun addMissing(string: String) {
            if (!finishedKnownCategories) return // this value gets set to true when we finish loading all categories in this object
            missingCategories.add(string)
            debugString(debugToggle) { "Unknown category: '$string'" }
        }
        private val registeredCategories = mutableMapOf<String, SkyBlockCategory>()
        //endregion

        public val categories: Collection<SkyBlockCategory> get() = registeredCategories.values

        public fun create(string: String, parseModifier: Boolean): SkyBlockCategory {
            val formatted = string.lowercase()
            return registeredCategories.getOrPut(formatted) {
                addMissing(formatted)
                val modifier = if (parseModifier) Modifier.byName(formatted.substringBefore(" ")) else null
                if (modifier == null) {
                    SkyBlockCategory(formatted)
                } else {
                    SkyBlockCategory(formatted.substringAfter(" ").trim(), modifier)
                }
            }
        }

        public fun create(string: String, @IntroducedAt("4.2.43") modifier: Modifier? = null): SkyBlockCategory {
            val formatted = if (modifier == null) string.lowercase()
            else "$modifier ${string.lowercase()}"
            return registeredCategories.getOrPut(formatted) {
                addMissing(formatted)
                SkyBlockCategory(formatted.trim(), modifier)
            }
        }

        public val NECKLACE: SkyBlockCategory = create("necklace")
        public val DUNGEON_NECKLACE: SkyBlockCategory = create("necklace", Modifier.DUNGEON)
        public val CLOAK: SkyBlockCategory = create("cloak")
        public val DUNGEON_CLOAK: SkyBlockCategory = create("cloak", Modifier.DUNGEON)
        public val BELT: SkyBlockCategory = create("belt")
        public val DUNGEON_BELT: SkyBlockCategory = create("belt", Modifier.DUNGEON)
        public val GLOVES: SkyBlockCategory = create("gloves")
        public val DUNGEON_GLOVES: SkyBlockCategory = create("gloves", Modifier.DUNGEON)
        public val BRACELET: SkyBlockCategory = create("bracelet")
        public val DUNGEON_BRACELET: SkyBlockCategory = create("bracelet", Modifier.DUNGEON)
        public val ARROW: SkyBlockCategory = create("arrow")
        public val ACCESSORY: SkyBlockCategory = create("accessory")
        public val DUNGEON_ACCESSORY: SkyBlockCategory = create("accessory", Modifier.DUNGEON)
        public val HATCESSORY: SkyBlockCategory = create("hatcessory")

        public val SWORD: SkyBlockCategory = create("sword")
        public val DUNGEON_SWORD: SkyBlockCategory = create("sword", Modifier.DUNGEON)
        public val SLAYER_SWORD: SkyBlockCategory = create("sword", Modifier.SLAYER)
        public val LONGSWORD: SkyBlockCategory = create("longsword")
        public val DUNGEON_LONGSWORD: SkyBlockCategory = create("longsword", Modifier.DUNGEON)
        public val BOW: SkyBlockCategory = create("bow")
        public val DUNGEON_BOW: SkyBlockCategory = create("bow", Modifier.DUNGEON)
        public val WAND: SkyBlockCategory = create("wand")
        public val DUNGEON_WAND: SkyBlockCategory = create("wand", Modifier.DUNGEON)
        public val AXE: SkyBlockCategory = create("axe")
        public val GAUNTLET: SkyBlockCategory = create("gauntlet")
        public val PICKAXE: SkyBlockCategory = create("pickaxe")
        public val DUNGEON_PICKAXE: SkyBlockCategory = create("pickaxe", Modifier.DUNGEON)
        public val SHOVEL: SkyBlockCategory = create("shovel")
        public val DRILL: SkyBlockCategory = create("drill")
        public val SHEARS: SkyBlockCategory = create("shears")

        public val HELMET: SkyBlockCategory = create("helmet")
        public val DUNGEON_HELMET: SkyBlockCategory = create("helmet", Modifier.DUNGEON)
        public val CHESTPLATE: SkyBlockCategory = create("chestplate")
        public val DUNGEON_CHESTPLATE: SkyBlockCategory = create("chestplate", Modifier.DUNGEON)
        public val LEGGINGS: SkyBlockCategory = create("leggings")
        public val DUNGEON_LEGGINGS: SkyBlockCategory = create("leggings", Modifier.DUNGEON)
        public val BOOTS: SkyBlockCategory = create("boots")
        public val DUNGEON_BOOTS: SkyBlockCategory = create("boots", Modifier.DUNGEON)

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
        public val DUNGEON_PASS: SkyBlockCategory = create("dungeon pass", parseModifier = false) // seems to be admin only, not a dungeon modifier

        public val DUNGEON_ITEM: SkyBlockCategory = create("item", Modifier.DUNGEON)
        public val ITEM: SkyBlockCategory = create("item") // there is no current item for this, but because "dungeon item" is a category, we add it here as well

        init {
            // we set this to true at the end so we can know what categories get registered from outside
            finishedKnownCategories = true
        }

        @Subscription
        private fun onCommands(event: RegisterSkyblockApiCommandsEvent) {
            event.registerWithCallback("unknown categories") {
                if (missingCategories.isEmpty()) {
                    Text.sendDebug("No unknown categories.")
                    return@registerWithCallback
                }
                McClient.clipboard = missingCategories.joinToString(prefix = "[", postfix = "]")
                val size = missingCategories.size
                Text.sendDebug("Copied $size unknown ${pluralize(size, "category", "categories")} to clipboard.")
            }
        }
    }

    public enum class Modifier(displayName: String? = null) {
        DUNGEON,
        SLAYER,
        ;

        public val displayName: String = displayName ?: name.lowercase()
        override fun toString(): String = displayName

        public companion object {
            public fun byName(name: String): Modifier? = entries.find { it.name.equals(name, true) }
        }
    }
}
