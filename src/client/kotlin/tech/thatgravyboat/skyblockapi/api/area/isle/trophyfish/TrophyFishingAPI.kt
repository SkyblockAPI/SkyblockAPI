package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import tech.thatgravyboat.skyblockapi.api.data.stored.TrophyFishStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.isle.TrophyFishCaughtEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object TrophyFishingAPI {

    private val chatGroup = RegexGroup.CHAT.group("trophy_api")
    private val inventoryGroup = RegexGroup.INVENTORY.group("trophy_api")

    private val trophyFishCaughtRegex = chatGroup.create(
        "caught",
        "♔ TROPHY FISH! You caught an? (?<type>.+?) (?<tier>${TrophyFishTier.entries.joinToString("|", transform = { it.name })})!",
    )

    private val trophyFishDescription = inventoryGroup.create(
        "description",
        "(?<tier>Diamond|Silver|Gold|Bronze) \\S+(?: \\((?<amount>\\d+)\\))?",
    )

    @Subscription
    @OnlyIn(SkyBlockIsland.CRIMSON_ISLE)
    fun onChat(event: ChatReceivedEvent.Pre) {
        val content = event.text.trim()
        trophyFishCaughtRegex.match(content, "type", "tier") { (type, tier) ->
            val fishTier = TrophyFishTier.valueOf(tier)
            val type = TrophyFishType.getByDisplayName(type) ?: return@match

            TrophyFishStorage.addCaught(type, fishTier)
            TrophyFishCaughtEvent(type, fishTier).post()
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.CRIMSON_ISLE)
    fun onInventory(event: InventoryChangeEvent) {
        if (event.title != "Trophy Fishing") return
        if (event.isInPlayerInventory) return
        if (!event.isInMainPart) return
        if (event.isSkyBlockFiller) return

        val byName = TrophyFishType.getByDisplayName(event.item.cleanName) ?: return
        val caught = mutableMapOf<TrophyFishTier, Int>()
        event.item.getRawLore().forEach {
            trophyFishDescription.match(it, "tier", "amount") { match ->
                val (tierName) = match
                val amount = match["amount"] ?: "0"
                val tier = TrophyFishTier.getByName(tierName)
                caught.put(tier, amount.toInt())
            }
        }
        TrophyFishStorage.setAmounts(byName, caught)
    }

    fun getCaught(type: TrophyFishType): Map<TrophyFishTier, Int> {
        return TrophyFishStorage.getCaught(type)
    }
}
