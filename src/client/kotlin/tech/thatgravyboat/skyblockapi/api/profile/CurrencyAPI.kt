package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Suppress("MemberVisibilityCanBePrivate")
@Module
object CurrencyAPI {

    private val bankSingleRegex = Regexes.create("tablist.widget.profile.bank.single", " Bank: (?<bank>[\\d,kmb]+)")
    private val bankCoopRegex = Regexes.create(
        "tablist.widget.profile.bank.coop",
        " Bank: (?<coop>\\.\\.\\.|[\\d,kmb]+) / (?<personal>[\\d,kmb]+)",
    )
    private val purseRegex = Regexes.create("scoreboard.currency.purse", "(?:Purse|Piggy): (?<purse>[\\d,kmb.]+)")
    private val motesRegex = Regexes.create("scoreboard.currency.motes", "Motes: (?<motes>[\\d,kmb]+)")

    var purse: Double = 0.0
        private set

    var bank: Long = 0
        private set

    var motes: Long = 0
        private set

    @Subscription
    fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        when (event.widget) {
            TabWidget.PROFILE -> {
                bankSingleRegex.anyMatch(event.new, "bank") { (bank) ->
                    this.bank = bank.parseFormattedLong()
                }
                bankCoopRegex.anyMatch(event.new, "coop", "personal") { (coop, personal) ->
                    this.bank = personal.parseFormattedLong() + coop.parseFormattedLong()
                }
            }

            else -> {}
        }
    }

    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        purseRegex.anyMatch(event.added, "purse") { (purse) ->
            this.purse = purse.parseFormattedDouble()
        }
        motesRegex.anyMatch(event.added, "motes") { (motes) ->
            this.motes = motes.parseFormattedLong()
        }
    }
}
