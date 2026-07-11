package tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.TrophyFrogStorage
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.atoll.TrophyFrogCaughtEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix

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

            Text.of {
                append("Caught: ")
                append(type.displayName)
                append(CommonText.SPACE)
                append(frogTier.nameSuffix)
            }.sendWithPrefix()
            TrophyFrogStorage.addCaught(type, frogTier)
            TrophyFrogCaughtEvent(type, frogTier).post()
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
        Text.of {
            append(byName.displayName)
            caught.forEach { (tier, tierAmount) ->
                append(CommonText.SPACE)
                append(tier.nameSuffix)
                append(": $tierAmount")
            }
        }.sendWithPrefix()
        TrophyFrogStorage.setAmounts(byName, caught)
    }

    // TODO: Data
    /*@OptIn(SkyBlockPvRequired::class)
    @Subscription
    @OnlyOnSkyBlock
    fun onPv(event: SkyBlockPvOpenedEvent) {
        val obtained = event.member["trophy_fish"].asMap { key, value ->
            if (!value.isJsonPrimitive) null to 0
            else key to value.asInt(0)
        }.filterKeysNotNull()
        var hasLoadedAny = false

        val grouped = obtained.entries.groupBy { group -> TrophyFrogType.entries.find { group.key.startsWith(it.internalName, true) } }.filterKeysNotNull()
        val unlocked = grouped.mapValues { entry ->
            val caught = getCaught(entry.key)
            entry.value.associate { value ->
                val tier = TrophyTier.entries.find { value.key.endsWith(it.name, true) }
                val previous = tier?.let(caught::get) ?: 0
                val value = value.value

                if (tier != null && value > previous) {
                    hasLoadedAny = true
                }

                tier to max(value, previous)
            }.filterKeysNotNull()
        }

        if (hasLoadedAny) {
            PvLoadingHelper.markLoaded(LoadedData.TROPHY_FISH)
        }
        unlocked.forEach(TrophyFrogStorage::setAmounts)
    }

    fun getCaught(type: TrophyFrogType): Map<TrophyTier, Int> {
        return TrophyFrogStorage.getCaught(type)
    }*/
}
