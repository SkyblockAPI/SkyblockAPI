package tech.thatgravyboat.skyblockapi.api.profile.community

import tech.thatgravyboat.skyblockapi.api.data.stored.CommunityCenterStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.modules.Module
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

    // TODO: Add museum progress
    val bitsPerCookie: Int get() = (BASE_COOKIE_BITS * (fameRank?.multiplier ?: 1.0)).toInt()

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
