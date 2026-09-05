package tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.TrophyFrogStorage
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.TrophyCaughtEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object TrophyFrogAPI {

    private val chatGroup = RegexGroup.CHAT.group("trophy_api")
    private val inventoryGroup = RegexGroup.INVENTORY.group("trophy_api")

    private val trophyFrogCaughtRegex = chatGroup.create(
        "frog_caught",
        "\uE02A TROPHY FROG! You caught an? (?<type>.+?) (?<tier>${TrophyTier.entries.joinToString("|", transform = { it.name })})!",
    )

    private val trophyFrogDescription = inventoryGroup.create(
        "frog_description",
        "(?<tier>Diamond|Silver|Gold|Bronze) \\S+(?: \\((?<amount>\\d+)\\))?",
    )

    @Subscription
    @OnlyIn(SkyBlockIsland.LOTUS_ATOLL)
    fun onChat(event: ChatReceivedEvent.Pre) {
        val content = event.text.trim()
        trophyFrogCaughtRegex.match(content, "type", "tier") { (type, tier) ->
            val frogTier = TrophyTier.valueOf(tier)
            val type = TrophyFrogType.getByDisplayName(type) ?: return@match

            TrophyFrogStorage.addCaught(type, frogTier)
            TrophyCaughtEvent.Frog(type, frogTier).post()
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.LOTUS_ATOLL)
    fun onInventory(event: InventoryChangeEvent) {
        if (event.title != "Trophy Frogs") return
        if (event.isInPlayerInventory) return
        if (!event.isInMainPart) return
        if (event.isSkyBlockFiller) return

        val byName = TrophyFrogType.getByDisplayName(event.item.cleanName) ?: return
        val caught = mutableMapOf<TrophyTier, Int>()
        event.item.getRawLore().forEach {
            trophyFrogDescription.match(it, "tier", "amount") { match ->
                val (tierName) = match
                val amount = match["amount"] ?: "0"
                val tier = TrophyTier.getByName(tierName)
                caught[tier] = amount.toInt()
            }
        }
        TrophyFrogStorage.setAmounts(byName, caught)
    }

    fun getCaught(type: TrophyFrogType): Map<TrophyTier, Int> {
        return TrophyFrogStorage.getCaught(type)
    }

    // TODO: If possible, load from pv too
}
