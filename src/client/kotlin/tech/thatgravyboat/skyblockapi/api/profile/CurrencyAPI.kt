package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

enum class PurseType {
    UNKNOWN,
    NORMAL,
    PIGGY,
}

@Module
@Suppress("MemberVisibilityCanBePrivate")
object CurrencyAPI {

    private val widgetGroup = RegexGroup.TABLIST_WIDGET.group("currency")
    private val gemsRegex = widgetGroup.create("area.gems", "(?i) Gems: (?<gems>[\\d,.kmb]+)")
    private val bankSingleRegex = widgetGroup.create("profile.bank.single", "(?i) Bank: (?<bank>[\\d,.kmb]+)")
    private val bankCoopRegex = widgetGroup.create(
        "profile.bank.coop",
        "(?i) Bank: (?<coop>\\.\\.\\.|[\\d,.kmb]+) / (?<personal>[\\d,.kmb]+)",
    )
    private val soulflowRegex = widgetGroup.create("profile.soulflow", "(?i) Soulflow: (?<soulflow>[\\d,.kmb]+)")

    private val currencyGroup = RegexGroup.SCOREBOARD.group("currency")
    private val purseRegex = currencyGroup.create("purse", "(?<type>Purse|Piggy): (?<purse>[\\d,.kmb]+).*")
    private val bitsRegex = currencyGroup.create("bits", "Bits: (?<bits>[\\d,.kmb]+).*")
    private val motesRegex = currencyGroup.create("motes", "Motes: (?<motes>[\\d,.kmb]+).*")
    private val copperRegex = currencyGroup.create("copper", "Copper: (?<copper>[\\d,.kmb]+).*")
    private val northStarsRegex = currencyGroup.create("northstars", "North Stars: (?<northstars>[\\d,.kmb]+).*")

    var purse: Double = 0.0
        private set

    var purseType: PurseType = PurseType.UNKNOWN
        private set

    var personalBank: Long = 0
        private set

    var coopBank: Long = 0
        private set

    val bank get() = personalBank + coopBank

    var motes: Long = 0
        private set

    var bits: Long = 0
        private set

    var gems: Long = 0
        private set

    var copper: Long = 0
        private set

    var northStars: Long = 0
        private set

    var soulflow: Long = 0
        private set

    @Subscription
    fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        when (event.widget) {
            TabWidget.AREA -> {
                gemsRegex.anyMatch(event.new, "gems") { (gems) ->
                    this.gems = gems.parseFormattedLong()
                }
            }

            TabWidget.PROFILE -> {
                bankSingleRegex.anyMatch(event.new, "bank") { (bank) ->
                    this.coopBank = bank.parseFormattedDouble().toLong()
                    this.personalBank = 0
                }
                bankCoopRegex.anyMatch(event.new, "coop", "personal") { (coop, personal) ->
                    this.coopBank = coop.parseFormattedDouble().toLong()
                    this.personalBank = personal.parseFormattedDouble().toLong()
                }
                soulflowRegex.anyMatch(event.new, "soulflow") { (soulflow) ->
                    this.soulflow = soulflow.parseFormattedLong()
                }
            }

            else -> return
        }
    }

    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        if (SkyBlockIsland.THE_RIFT.inIsland()) {
            motesRegex.anyMatch(event.added, "motes") { (motes) ->
                this.motes = motes.parseFormattedLong()
            }
        } else {
            if (SkyBlockIsland.JERRYS_WORKSHOP.inIsland()) {
                northStarsRegex.anyMatch(event.added, "northstars") { (northstars) ->
                    this.northStars = northstars.parseFormattedLong()
                }
            } else if (SkyBlockIsland.GARDEN.inIsland()) {
                copperRegex.anyMatch(event.added, "copper") { (copper) ->
                    this.copper = copper.parseFormattedLong()
                }
            }
            purseRegex.anyMatch(event.added, "purse") { (type, purse) ->
                this.purse = purse.parseFormattedDouble()
                this.purseType = when (type.lowercase()) {
                    "purse" -> PurseType.NORMAL
                    "piggy" -> PurseType.PIGGY
                    else -> PurseType.UNKNOWN
                }
            }
            bitsRegex.anyMatch(event.added, "bits") { (bits) ->
                // Has a .0 if below 1k
                this.bits = bits.parseFormattedDouble().toLong()
            }
        }
    }

    private fun reset() {
        purse = 0.0
        purseType = PurseType.UNKNOWN
        personalBank = 0
        coopBank = 0
        motes = 0
        bits = 0
        copper = 0
        northStars = 0
    }

    @Subscription
    fun onProfileChange(event: ProfileChangeEvent) {
        reset()
    }
}
