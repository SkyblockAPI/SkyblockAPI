package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.asReversedIterator
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Module
object LoreDataTypes {

    internal val dataTypeGroup = Regexes.group("datatype")

    private val fuelRegex = dataTypeGroup.create("fuel", "Fuel: (?<fuel>[\\d,kmb]+)/(?<max>[\\d,kmb]+)")
    private val rightClickAbilityRegex = dataTypeGroup.create("right_click_ability", "(?:⦾ )?Ability: (?<ability>[\\w ]+) {2}RIGHT CLICK")
    private val manaCostRegex = dataTypeGroup.create("mana_cost", "Mana Cost: (?<mana>[\\d,kmb]+)")
    private val cooldownRegex = dataTypeGroup.create("cooldown", "Cooldown: (?<cooldown>\\d+)s")
    private val snowballsRegex = dataTypeGroup.create("snowballs", "Snowballs: (?<snowballs>[\\d,kmb]+)/(?<max>[\\d,kmb]+)")
    private val dungeonBreakerRegex = dataTypeGroup.create("dungeonbreaker", "Charges: (?<current>\\d+)/(?<max>\\d+)⸕")
    private val waterRegex = dataTypeGroup.create("water_level", "Water: (?<current>[\\d,kmb]+)/(?<max>[\\d,kmb]+)")
    private val selectedArrowRegex = dataTypeGroup.create("arrow", "^Selected: (?<type>.+)$")
    private val soulboundRegex = dataTypeGroup.create("soulbound", "\\* (?<coop>Co-op )?Soulbound \\*")

    val FUEL: DataType<Pair<Int, Int>> = DataType.of("fuel") {
        var output: Pair<Int, Int>? = null
        fuelRegex.anyMatch(it.getRawLore(), "fuel", "max") { (fuel, max) ->
            output = fuel.parseFormattedInt() to max.parseFormattedInt()
        }
        output
    }

    val SNOWBALLS: DataType<Pair<Int, Int>> = DataType.of("snowballs") {
        var output: Pair<Int, Int>? = null
        snowballsRegex.anyMatch(it.getRawLore(), "snowballs", "max") { (snowballs, max) ->
            output = snowballs.parseFormattedInt() to max.parseFormattedInt()
        }
        output
    }

    val RIGHT_CLICK_MANA_ABILITY: DataType<Pair<String, Int>> = DataType.of("right_click_mana_ability") {
        var outputAbility: String? = null
        var outputMana: Int? = null

        for (lore in it.getRawLore()) {
            rightClickAbilityRegex.match(lore, "ability") { (ability) -> outputAbility = ability }
            if (outputAbility != null && manaCostRegex.match(lore, "mana") { (mana) -> outputMana = mana.parseFormattedInt() }) break
        }

        if (outputAbility != null && outputMana != null) outputAbility to outputMana else null
    }

    val COOLDOWN_ABILITY: DataType<Pair<String, Duration>> = DataType.of("cooldown_ability") {
        var outputAbility: String? = null
        var outputDuration: Duration? = null

        for (lore in it.getRawLore()) {
            rightClickAbilityRegex.match(lore, "ability") { (ability) -> outputAbility = ability }
            if (outputAbility != null && cooldownRegex.match(lore, "cooldown") { (cooldown) -> outputDuration = cooldown.toLongValue().seconds }) break
        }

        if (outputAbility != null && outputDuration != null) outputAbility to outputDuration else null
    }

    private fun getRarityLine(stack: ItemStack): Pair<String, SkyBlockRarity>? {
        return getRarityLine(stack[DataComponents.LORE], DataTypes.RECOMBOBULATOR.factory(stack) == true)
    }

    internal fun getRarityLine(lore: ItemLore?, isUpgraded: Boolean = false): Pair<String, SkyBlockRarity>? {
        val lines = lore?.lines()?.map { it.stripped }?.asReversedIterator() ?: return null
        for (line in lines) {
            val rarityLine = (if (isUpgraded) line.drop(2).dropLast(2).trim() else line.trim()).removePrefix("SHINY ")
            val rarity = SkyBlockRarity.entries.firstOrNull { rarity -> rarityLine.startsWith(rarity.displayName.uppercase()) }
            if (rarity != null) {
                return rarityLine to rarity
            }
        }
        return null
    }

    val RARITY: DataType<SkyBlockRarity> = DataType.of("rarity") {
        getRarityLine(it)?.second ?: GenericDataTypes.PET_DATA.factory(it)?.rarity
    }

    val CATEGORY: DataType<SkyBlockCategory> = DataType.of("category") {
        getRarityLine(it)?.let { line ->
            line.first.removePrefix(line.second.displayName.uppercase()).trim()
        }?.let(SkyBlockCategory::create)
    }

    val DUNGEONBREAKER_CHARGES: DataType<Pair<Int, Int>> = DataType.of("dungeon_breaker_charges") {
        if (DataTypes.ID.factory(it) != "DUNGEONBREAKER") return@of null

        var output: Pair<Int, Int>? = null
        dungeonBreakerRegex.anyMatch(it.getRawLore(), "current", "max") { (current, max) ->
            output = current.parseFormattedInt() to max.parseFormattedInt()
        }
        output
    }

    val WATER_LEVEL: DataType<Pair<Int, Int>> = DataType.of("water_level") {
        var output: Pair<Int, Int>? = null
        waterRegex.anyMatch(it.getRawLore(), "current", "max") { (current, max) ->
            output = current.parseFormattedInt() to max.parseFormattedInt()
        }
        output
    }

    val SELECTED_ARROW: DataType<SkyBlockId> = DataType.of("selected_arrow") {
        if (DataTypes.ID.factory(it) != "ARROW_SWAPPER") return@of null

        var output: SkyBlockId? = null
        selectedArrowRegex.anyMatch(it.getRawLore(), "type") { (type) ->
            output = SkyBlockId.fromName(type, dropLast = false)
        }
        output
    }

    val SOULBOUND: DataType<SoulboundType> = DataType.of("soulbound") {
        val matches = it.getRawLore().reversed().firstNotNullOfOrNull(soulboundRegex::matchEntire) ?: return@of null
        if (matches.groups["coop"] != null) SoulboundType.COOP else SoulboundType.SOLO
    }

    enum class SoulboundType {
        SOLO,
        COOP;
    }
}
