package tech.thatgravyboat.skyblockapi.api.profile.community

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryFullyLoadedEvent
import tech.thatgravyboat.skyblockapi.api.profile.FameRank
import tech.thatgravyboat.skyblockapi.api.profile.FameRanks
import tech.thatgravyboat.skyblockapi.api.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object CommunityCenterAPI {


    private val gemsRegex = Regexes.create("tablist.widget.area.gems", " Gems: (?<gems>[\\d,kmb]+)")
    private val bitsRegex = Regexes.create("scoreboard.currency.bits", "Bits: (?<bits>[\\d,kmb]+)")
    private val cookieAteRegex = Regexes.create("chat.currency.cookie.ate", "You consumed a Booster Cookie!.*")
    private val bitsAvailableRegex = Regexes.create("inventory.currency.bits.available", "Bits Available: (?<bits>[\\d,kmb]+).*")
    private val fameRankRegex = Regexes.create("inventory.currency.fame.rank", "Your rank: (?<rank>.*)")

    private const val BASE_COOKIE_BITS = 4800

    var bits: Long = 0
        private set

    var bitsAvailable: Long = 0
        private set(bits) {
            val profileName = ProfileAPI.profileName ?: return
            CommunityCenterStorage.setBitsAvailable(profileName, bits)
            field = bits
        }
        get() {
            val profileName = ProfileAPI.profileName ?: return 0
            return CommunityCenterStorage.getBitsAvailable(profileName)
        }

    var fameRank: FameRank? = null
        private set(rank) {
            val uuid = McClient.self.player?.uuid ?: return
            CommunityCenterStorage.setRank(uuid, rank)
            field = rank
        }
        get() {
            val uuid = McClient.self.player?.uuid ?: return null
            return CommunityCenterStorage.getRank(uuid)
        }


    var gems: Long = 0
        private set


    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        bitsRegex.anyMatch(event.added, "bits") { (bits) ->
            this.bits = bits.parseFormattedLong()
        }
    }

    @Subscription
    fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        when (event.widget) {
            TabWidget.AREA -> {
                gemsRegex.anyMatch(event.new, "gems") { (gems) ->
                    this.gems = gems.parseFormattedLong()
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
        when (event.title.string) {
            "SkyBlock Menu" -> handleSkyblockMenu(event)
            "Booster Cookie" -> handleBoosterCookieMenu(event)
            else -> {}
        }
    }

    private fun handleSkyblockMenu(event: InventoryFullyLoadedEvent) {
        val cookieLore = event.itemStacks.firstOrNull { it.hoverName.string == "Booster Cookie" }?.getRawLore()
        if (cookieLore != null) {
            bitsAvailableRegex.anyMatch(cookieLore, "bits") { (bits) ->
                bitsAvailable = bits.parseFormattedLong()
            }
        }
    }

    private fun handleBoosterCookieMenu(event: InventoryFullyLoadedEvent) {
        val fameRankLore = event.itemStacks.firstOrNull { it.hoverName.string == "Fame Rank" }?.getRawLore()
        val bitsLore = event.itemStacks.firstOrNull { it.hoverName.string == "Bits" }?.getRawLore()

        if (fameRankLore != null) {
            fameRankRegex.anyMatch(fameRankLore, "rank") { (rank) ->
                fameRank = FameRanks.registeredFameRanks.values.firstOrNull { it.name == rank }
            }
        }

        if (bitsLore != null) {
            bitsAvailableRegex.anyMatch(bitsLore, "bits") { (bits) ->
                bitsAvailable = bits.parseFormattedLong()
            }
        }
    }
}
