package tech.thatgravyboat.skyblockapi.api.profile.currency

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.CommunityCenterStorage
import tech.thatgravyboat.skyblockapi.api.data.stored.CurrencyStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.CurrencyUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import kotlin.reflect.KMutableProperty0

enum class PurseType(scoreboardName: String? = null) {
    NORMAL("PURSE"),
    PIGGY,
    UNKNOWN,
    ;

    private val scoreboardName: String = scoreboardName ?: name

    companion object {
        fun fromName(name: String): PurseType = entries.find { it.scoreboardName.equals(name, true) } ?: UNKNOWN
    }
}

private typealias CurrencyEvent = CurrencyUpdateEvent<*>

@Module
@Suppress("MemberVisibilityCanBePrivate")
object CurrencyAPI {

    private val widgetGroup = RegexGroup.TABLIST_WIDGET.group("currency")
    private val bankSingleRegex = widgetGroup.create("profile.bank.single", "(?i) Bank: (?<bank>(?i)[\\d,.kmb]+)")
    private val bankCoopRegex = widgetGroup.create(
        "profile.bank.coop",
        " Bank: (?<coop>\\.\\.\\.|(?i)[\\d,.kmb]+) / (?<personal>(?i)[\\d,.kmb]+)",
    )
    private val soulflowRegex = widgetGroup.create("profile.soulflow", " Soulflow: (?<soulflow>[\\d,.kmb]+)")
    private val gemsRegex = widgetGroup.create("gems", " Gems: (?<gems>(?i)[\\d,.kmb]+)")

    private val currencyGroup = RegexGroup.SCOREBOARD.group("currency")
    private val purseRegex = currencyGroup.create("purse", "^(?<type>Purse|Piggy): (?<purse>(?i)[\\d,.kmb]+)")
    private val bitsRegex = currencyGroup.create("bits", "^Bits: (?<bits>(?i)[\\d,.kmb]+)")
    private val motesRegex = currencyGroup.create("motes", "^Motes: (?<motes>(?i)[\\d,.kmb]+)")
    private val copperRegex = currencyGroup.create("copper", "^Copper: (?<copper>(?i)[\\d,.kmb]+)")
    private val sowdustRegex = currencyGroup.create("sowdust", "^Sowdust: (?<sowdust>(?i)[\\d,.kmb]+)")
    private val northStarsRegex = currencyGroup.create("northstars", "^North Stars: (?<northstars>(?i)[\\d,.kmb]+)")

    var purse: Double by CurrencyStorage::purse
        private set

    var purseType: PurseType by CurrencyStorage::purseType
        private set

    var personalBank: Long by CurrencyStorage::personalBank
        private set

    var coopBank: Long by CurrencyStorage::coopBank
        private set

    val bank get() = personalBank + coopBank

    var motes: Long by CurrencyStorage::motes
        private set

    var bits: Long by CurrencyStorage::bits
        private set

    var copper: Long by CurrencyStorage::copper
        private set

    var sowdust: Long by CurrencyStorage::sowdust
        private set

    var northStars: Long by CurrencyStorage::northStars
        private set

    var gems: Long by CommunityCenterStorage::gems
        private set

    // TODO: move somewhere else, since soulflow isn't really a currency
    var soulflow: Long by CurrencyStorage::soulflow
        private set

    @Subscription
    @OnlyWidget(TabWidget.PROFILE, TabWidget.AREA)
    fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        when (event.widget) {
            TabWidget.PROFILE -> {
                bankSingleRegex.anyMatch(event.new, "bank") { (bank) ->
                    this.coopBank = post(bank, this.coopBank, CurrencyEvent::CoopBank)
                    this.personalBank = post(0, this.personalBank, CurrencyEvent::Bank)
                }
                bankCoopRegex.anyMatch(event.new, "coop", "personal") { (coop, personal) ->
                    this.coopBank = post(coop, this.coopBank, CurrencyEvent::CoopBank)
                    this.personalBank = post(personal, this.personalBank, CurrencyEvent::Bank)
                }
                soulflowRegex.anyMatch(event.new, "soulflow") { (soulflow) ->
                    this.soulflow = soulflow.parseFormattedLong()
                }
            }
            TabWidget.AREA -> {
                gemsRegex.findCurrency(event.new, "gems", ::gems, CurrencyEvent::Gems)
            }
            else -> return
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        if (SkyBlockIsland.THE_RIFT.inIsland()) {
            // Has a decimal place if obtained via mcgrubber burgers
            motesRegex.findCurrency(event.added, "motes", ::motes, CurrencyEvent::Motes)
        } else {
            if (SkyBlockIsland.JERRYS_WORKSHOP.inIsland()) {
                northStarsRegex.findCurrency(event.added, "northstars", ::northStars, CurrencyEvent::NorthStars)
            } else if (SkyBlockIsland.GARDEN.inIsland()) {
                copperRegex.findCurrency(event.added, "copper", ::copper, CurrencyEvent::Copper)
                sowdustRegex.findCurrency(event.added, "sowdust", ::sowdust, CurrencyEvent::SowDust)
            }
            purseRegex.anyFound(event.added, "type", "purse") { (type, purse) ->
                this.purseType = PurseType.fromName(type)
                this.purse = post(purse.parseFormattedDouble(), this.purse, CurrencyEvent::Purse)
            }
            bitsRegex.findCurrency(event.added, "bits", ::bits, CurrencyEvent::Bits)
        }
    }

    private inline fun Regex.findCurrency(
        added: List<String>,
        group: String,
        property: KMutableProperty0<Long>,
        crossinline event: (Long, Long) -> CurrencyUpdateEvent<Long>,
    ) = anyFound(added, group) { (newValue) -> property.set(post(newValue, property(), event)) }

    // Helper method for specifically parsing longs from a string, since most of them are like this
    private inline fun post(new: String, old: Long, event: (Long, Long) -> CurrencyUpdateEvent<Long>): Long {
        return post(new.parseFormattedLong(), old, event)
    }

    private inline fun <N : Number> post(new: N, old: N, event: (N, N) -> CurrencyUpdateEvent<N>): N {
        if (new != old) event(new, old).post()
        return new
    }
}
