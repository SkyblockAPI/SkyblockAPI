package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import tech.thatgravyboat.skyblockapi.api.data.SkyblockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Module
object LoreDataTypes {

    private val fuelRegex = Regexes.create("datatype.fuel", "Fuel: (?<fuel>[\\d,kmb]+)/(?<max>[\\d,kmb]+)")
    private val rightClickAbilityRegex = Regexes.create("datatype.right_click_ability", "Ability: (?<ability>[\\w ]+) {2}RIGHT CLICK")
    private val manaCostRegex = Regexes.create("datatype.mana_cost", "Mana Cost: (?<mana>[\\d,kmb]+)")
    private val cooldownRegex = Regexes.create("datatype.cooldown", "Cooldown: (?<cooldown>\\d+)s")
    private val snowballsRegex = Regexes.create("datatype.snowballs", "Snowballs: (?<snowballs>[\\d,kmb]+)/(?<max>[\\d,kmb]+)")

    val FUEL: DataType<Pair<Int, Int>> = DataType("fuel") {
        var output: Pair<Int, Int>? = null
        fuelRegex.anyMatch(it.getRawLore(), "fuel", "max") { (fuel, max) ->
            output = fuel.parseFormattedInt() to max.parseFormattedInt()
        }
        output
    }

    val SNOWBALLS: DataType<Pair<Int, Int>> = DataType("snowballs") {
        var output: Pair<Int, Int>? = null
        snowballsRegex.anyMatch(it.getRawLore(), "snowballs", "max") { (snowballs, max) ->
            output = snowballs.parseFormattedInt() to max.parseFormattedInt()
        }
        output
    }

    val RIGHT_CLICK_MANA_ABILITY: DataType<Pair<String, Int>> = DataType("right_click_mana_ability") {
        var outputAbility: String? = null
        var outputMana: Int? = null

        for (lore in it.getRawLore()) {
            rightClickAbilityRegex.match(lore, "ability") { (ability) -> outputAbility = ability }
            if (manaCostRegex.match(lore, "mana") { (mana) -> outputMana = mana.parseFormattedInt() }) break
        }

        if (outputAbility != null && outputMana != null) outputAbility!! to outputMana!! else null
    }

    val COOLDOWN_ABILITY: DataType<Pair<String, Duration>> = DataType("cooldown_ability") {
        var outputAbility: String? = null
        var outputDuration: Duration? = null

        for (lore in it.getRawLore()) {
            rightClickAbilityRegex.match(lore, "ability") { (ability) -> outputAbility = ability }
            if (cooldownRegex.match(lore, "cooldown") { (cooldown) -> outputDuration = cooldown.toLongValue().seconds }) break
        }

        if (outputAbility != null && outputDuration != null) outputAbility!! to outputDuration!! else null
    }

    val RARITY: DataType<SkyblockRarity> = DataType("rarity") {
        val lastLine = it.getRawLore().lastOrNull() ?: return@DataType null
        SkyblockRarity.entries.firstOrNull { rarity -> lastLine.trim().startsWith(rarity.name, ignoreCase = true) }
    }

    @Subscription
    fun onDataTypeRegistration(event: RegisterDataTypesEvent) {
        event.register(FUEL)
        event.register(SNOWBALLS)
        event.register(RIGHT_CLICK_MANA_ABILITY)
        event.register(COOLDOWN_ABILITY)
        event.register(RARITY)
    }
}
