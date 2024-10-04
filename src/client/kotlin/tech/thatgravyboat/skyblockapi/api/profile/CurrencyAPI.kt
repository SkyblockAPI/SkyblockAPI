package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryFullyLoadedEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object CurrencyAPI {

    private val gemsRegex = Regexes.create("tablist.widget.area.gems", " Gems: (?<gems>[\\d,kmb]+)")
    private val bankSingleRegex = Regexes.create("tablist.widget.profile.bank.single", " Bank: (?<bank>[\\d,kmb]+)")
    private val bankCoopRegex = Regexes.create(
        "tablist.widget.profile.bank.coop",
        " Bank: (?<coop>\\.\\.\\.|[\\d,kmb]+) / (?<personal>[\\d,kmb]+)",
    )
    private val purseRegex = Regexes.create("scoreboard.currency.purse", "Purse: (?<purse>[\\d,kmb.]+)")
    private val bitsRegex = Regexes.create("scoreboard.currency.bits", "Bits: (?<bits>[\\d,kmb]+)")
    private val motesRegex = Regexes.create("scoreboard.currency.motes", "Motes: (?<motes>[\\d,kmb]+)")
    private val cookieAteRegex = Regexes.create("chat.currency.cookie.ate", "You consumed a Booster Cookie!.*")
    private val bitsAvailableRegex = Regexes.create("inventory.currency.bits.available", "Bits Available: (?<bits>[\\d,kmb]+).*")

    private const val BASE_COOKIE_BITS = 4800

    var purse: Double = 0.0
        private set

    var bank: Long = 0
        private set

    var motes: Long = 0
        private set

    var bits: Long = 0
        private set

    var bitsAvailable: Long = 0
        private set

    var fameRank: FameRank? = null
        private set

    var gems: Long = 0
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
    fun onChat(event: ChatReceivedEvent) {
        if (cookieAteRegex.matches(event.text)) {
            bitsAvailable += (BASE_COOKIE_BITS * (fameRank?.multiplier ?: 1.0)).toInt()
        }
    }

    @Subscription
    fun onInventoryFullyLoaded(event: InventoryFullyLoadedEvent) {
        println("a " + event.title.string + " a")
        when (event.title.string) {
            "SkyBlock Menu" -> handleSkyblockMenu(event)
            else -> {}
        }
    }

    private fun handleSkyblockMenu(event: InventoryFullyLoadedEvent) {
        println(event.itemStacks.map { it.displayName.string })
        val cookieItemStack = event.itemStacks.firstOrNull { it.displayName.string == "Booster Cookie" }
        if (cookieItemStack != null) {
            println(cookieItemStack.getRawLore())
            bitsAvailableRegex.anyMatch(cookieItemStack.getRawLore(), "bits") { (bits) ->
                bitsAvailable = bits.parseFormattedLong()
                println("Bits available: $bitsAvailable")
            }
        }
    }

    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        purseRegex.anyMatch(event.added, "purse") { (purse) ->
            this.purse = purse.parseFormattedDouble()
        }
        bitsRegex.anyMatch(event.added, "bits") { (bits) ->
            this.bits = bits.parseFormattedLong()
        }
        motesRegex.anyMatch(event.added, "motes") { (motes) ->
            this.motes = motes.parseFormattedLong()
        }
    }
}
