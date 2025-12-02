package tech.thatgravyboat.skyblockapi.api.profile.community

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.CommunityCenterStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains

private const val BASE_COOKIE_BITS = 4800

@Module
@Suppress("MemberVisibilityCanBePrivate")
object CommunityCenterAPI {

    internal val cookieAteRegex = RegexGroup.CHAT.create("communitycenter.cookie.ate", "^You consumed a Booster Cookie!")
    private val bitsAvailableRegex = RegexGroup.INVENTORY.create("communitycenter.bits.available", "Bits Available: (?<bits>[\\d,kmb]+).*")
    private val fameRankRegex = RegexGroup.INVENTORY.create("communitycenter.fame.rank", "Your rank: (?<rank>.*)")
    private val gemsRegex = RegexGroup.TABLIST.create("currency.area.gems", "(?i) Gems: (?<gems>[\\d,.kmb]+)")
    var bitsAvailable: Long
        get() = CommunityCenterStorage.bitsAvailable
        private set(value) {
            CommunityCenterStorage.bitsAvailable = value
        }

    var fameRank: FameRank?
        get() = CommunityCenterStorage.rank
        private set(value) {
            CommunityCenterStorage.rank = value
        }

    var gems: Long
        get() = CommunityCenterStorage.gems
        private set(value) {
            CommunityCenterStorage.gems = value
        }

    val bitsPerCookie: Int
        get() {
            val museumBonus = 1 + MuseumAPI.milestone * 0.01 // 1% per level
            return (BASE_COOKIE_BITS * museumBonus * (fameRank?.multiplier ?: 1.0)).toInt()
        }


    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (cookieAteRegex.contains(event.text)) {
            bitsAvailable += bitsPerCookie
        }
    }

    @Subscription
    fun onInventoryFullyLoaded(event: ContainerInitializedEvent) {
        when (event.title) {
            "SkyBlock Menu" -> handleSkyBlockMenu(event)
            "Booster Cookie" -> handleBoosterCookieMenu(event)
            else -> return
        }
    }

    @Subscription
    @OnlyWidget(TabWidget.AREA)
    fun onTabWidget(event: TabWidgetChangeEvent) {
        gemsRegex.anyMatch(event.new, "gems") { (gems) ->
            this.gems = gems.parseFormattedLong()
        }
    }

    private fun handleSkyBlockMenu(event: ContainerInitializedEvent) {
        val cookieLore = event.itemStacks.find { it.cleanName == "Booster Cookie" }?.getRawLore() ?: return
        bitsAvailableRegex.anyMatch(cookieLore, "bits") { (bits) ->
            bitsAvailable = bits.parseFormattedLong()
        }
    }

    private fun handleBoosterCookieMenu(event: ContainerInitializedEvent) {
        val fameRankLore = event.itemStacks.find { it.cleanName == "Fame Rank" }?.getRawLore()
        val bitsLore = event.itemStacks.find { it.cleanName == "Bits" }?.getRawLore()

        if (fameRankLore != null) {
            fameRankRegex.anyMatch(fameRankLore, "rank") { (rank) ->
                fameRank = FameRanks.getByName(rank)
            }
        }

        if (bitsLore != null) {
            bitsAvailableRegex.anyMatch(bitsLore, "bits") { (bits) ->
                bitsAvailable = bits.parseFormattedLong()
            }
        }
    }
}
