package tech.thatgravyboat.skyblockapi.api.profile.community

import tech.thatgravyboat.skyblockapi.api.data.stored.CommunityCenterStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

private const val BASE_COOKIE_BITS = 4800

@Module
@Suppress("MemberVisibilityCanBePrivate")
object CommunityCenterAPI {

    private val cookieAteRegex = RegexGroup.CHAT.create("communitycenter.cookie.ate", "You consumed a Booster Cookie!.*")
    private val bitsAvailableRegex = RegexGroup.INVENTORY.create("communitycenter.bits.available", "Bits Available: (?<bits>[\\d,kmb]+).*")
    private val fameRankRegex = RegexGroup.INVENTORY.create("communitycenter.fame.rank", "Your rank: (?<rank>.*)")

    var bitsAvailable: Long
        private set(bits) {
            val profileName = ProfileAPI.profileName ?: return
            CommunityCenterStorage.setBitsAvailable(profileName, bits)
        }
        get() {
            val profileName = ProfileAPI.profileName ?: return 0
            return CommunityCenterStorage.getBitsAvailable(profileName)
        }

    var fameRank: FameRank?
        private set(rank) = CommunityCenterStorage.setRank(McPlayer.uuid, rank)
        get() = CommunityCenterStorage.getRank(McPlayer.uuid)

    @Subscription
    fun onChat(event: ChatReceivedEvent) {
        if (cookieAteRegex.matches(event.text)) {
            bitsAvailable += (BASE_COOKIE_BITS * (fameRank?.multiplier ?: 1.0)).toInt()
        }
    }

    @Subscription
    fun onInventoryFullyLoaded(event: ContainerInitializedEvent) {
        when (event.title.string) {
            "SkyBlock Menu" -> handleSkyBlockMenu(event)
            "Booster Cookie" -> handleBoosterCookieMenu(event)
            else -> {}
        }
    }

    private fun handleSkyBlockMenu(event: ContainerInitializedEvent) {
        val cookieLore = event.itemStacks.firstOrNull { it.hoverName.string == "Booster Cookie" }?.getRawLore()
        if (cookieLore != null) {
            bitsAvailableRegex.anyMatch(cookieLore, "bits") { (bits) ->
                bitsAvailable = bits.parseFormattedLong()
            }
        }
    }

    private fun handleBoosterCookieMenu(event: ContainerInitializedEvent) {
        val fameRankLore = event.itemStacks.firstOrNull { it.hoverName.string == "Fame Rank" }?.getRawLore()
        val bitsLore = event.itemStacks.firstOrNull { it.hoverName.string == "Bits" }?.getRawLore()

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
