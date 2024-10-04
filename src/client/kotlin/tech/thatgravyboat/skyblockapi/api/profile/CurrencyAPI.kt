package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object CurrencyAPI {

    private val widgetGroup = Regexes.group("tablist.widget")
    private val gemsRegex = widgetGroup.create("area.gems", " Gems: (?<gems>[\\d,kmb]+)")
    private val bankSingleRegex = widgetGroup.create("profile.bank.single", " Bank: (?<bank>[\\d,kmb]+)")
    private val bankCoopRegex = widgetGroup.create(
        "profile.bank.coop",
        " Bank: (?<coop>\\.\\.\\.|[\\d,kmb]+) / (?<personal>[\\d,kmb]+)",
    )

    private val currencyGroup = Regexes.group("scoreboard.currency")
    private val purseRegex = currencyGroup.create("purse", "(?:Purse|Piggy): (?<purse>[\\d,kmb.]+)")
    private val bitsRegex = currencyGroup.create("bits", "Bits: (?<bits>[\\d,kmb]+)")
    private val motesRegex = currencyGroup.create("motes", "Motes: (?<motes>[\\d,kmb]+)")
    private val copperRegex = currencyGroup.create("copper", "Copper: (?<copper>[\\d,kmb]+)")
    private val northStarsRegex = currencyGroup.create("northstars", "North Stars: (?<northstars>[\\d,kmb]+)")

    var purse: Double = 0.0
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
                    this.coopBank = bank.parseFormattedLong()
                    this.personalBank = 0
                }
                bankCoopRegex.anyMatch(event.new, "coop", "personal") { (coop, personal) ->
                    this.coopBank = coop.parseFormattedLong()
                    this.personalBank = personal.parseFormattedLong()
                }
            }
            else -> {}
        }
    }

    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        if (SkyblockIsland.THE_RIFT.inIsland()) {
            motesRegex.anyMatch(event.added, "motes") { (motes) ->
                this.motes = motes.parseFormattedLong()
            }
        } else {
            if (SkyblockIsland.JERRYS_WORKSHOP.inIsland()) {
                northStarsRegex.anyMatch(event.added, "northstars") { (northstars) ->
                    this.northStars = northstars.parseFormattedLong()
                }
            } else if (SkyblockIsland.GARDEN.inIsland()) {
                copperRegex.anyMatch(event.added, "copper") { (copper) ->
                    this.copper = copper.parseFormattedLong()
                }
            }
            purseRegex.anyMatch(event.added, "purse") { (purse) ->
                this.purse = purse.parseFormattedDouble()
            }
            bitsRegex.anyMatch(event.added, "bits") { (bits) ->
                this.bits = bits.parseFormattedLong()
            }
        }
    }

    private fun reset() {
        purse = 0.0
        personalBank = 0
        coopBank = 0
        motes = 0
        bits = 0
        gems = 0
        copper = 0
        northStars = 0
    }

    @Subscription
    fun onProfileChange(event: ProfileChangeEvent) {
        reset()
    }
}
