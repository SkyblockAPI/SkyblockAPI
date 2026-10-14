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
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen

@Module
public object TrophyFrogAPI {

    private val chatGroup = RegexGroup.CHAT.group("trophy_api")
    private val inventoryGroup = RegexGroup.INVENTORY.group("trophy_api")

    private val singleTrophyFrogCaughtRegex = chatGroup.create(
        "frog_caught_single",
        "\uE02A TROPHY FROG! You caught(?: an?)? (?<type>.+?) (?<tier>${TrophyTier.entries.joinToString("|", transform = { it.name })})!",
    )

    private val multiTrophyFrogCaughtRegex = chatGroup.create(
        "frog_caught_multi",
        "\uE02A TROPHY FROG! You caught(?: an?)? (?<type>.+?) (?<tier>${TrophyTier.entries.joinToString("|", transform = { it.name })})!",
    )

    private val trophyFrogDescription = inventoryGroup.create(
        "frog_description",
        "(?<tier>Diamond|Silver|Gold|Bronze) \\S+(?: \\((?<amount>\\d+)\\))?",
    )

    @Subscription
    @OnlyIn(SkyBlockIsland.LOTUS_ATOLL)
    internal fun onChat(event: ChatReceivedEvent.Pre) {
        val content = event.text.trim()
        matchWhen(content) {
            case(singleTrophyFrogCaughtRegex, "type", "tier") { (type, tier) ->
                val fishTier = TrophyTier.valueOf(tier)
                val type = TrophyFrogType.getByDisplayName(type) ?: return@case

                TrophyFrogStorage.addCaught(type, fishTier)
                TrophyCaughtEvent.Frog(type, fishTier).post()
            }
            case(multiTrophyFrogCaughtRegex, "type", "tier", "amount") { (type, tier, amount) ->
                val fishTier = TrophyTier.valueOf(tier)
                val type = TrophyFrogType.getByDisplayName(type) ?: return@case
                val amount = amount.toIntOrNull() ?: return@case

                TrophyFrogStorage.addCaught(type, fishTier)
                TrophyCaughtEvent.Frog(type, fishTier, amount).post()
            }
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.LOTUS_ATOLL)
    internal fun onInventory(event: InventoryChangeEvent) {
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

    public fun getCaught(type: TrophyFrogType): Map<TrophyTier, Int> {
        return TrophyFrogStorage.getCaught(type)
    }

    // TODO: When possible, load from pv too
}
