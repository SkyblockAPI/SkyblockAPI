package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.TrophyFishStorage
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.TrophyTier
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.isle.TrophyFishCaughtEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.remote.LoadedData
import tech.thatgravyboat.skyblockapi.api.remote.PvLoadingHelper
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import kotlin.math.max

@Module
object TrophyFishingAPI {

    private val chatGroup = RegexGroup.CHAT.group("trophy_api")
    private val inventoryGroup = RegexGroup.INVENTORY.group("trophy_api")

    private val trophyFishCaughtRegex = chatGroup.create(
        "fish_caught",
        "\uE02A TROPHY FISH! You caught an? (?<type>.+?) (?<tier>${TrophyTier.entries.joinToString("|", transform = { it.name })})!",
    )

    private val trophyFishDescription = inventoryGroup.create(
        "fish_description",
        "(?<tier>Diamond|Silver|Gold|Bronze) \\S+(?: \\((?<amount>\\d+)\\))?",
    )

    @Subscription
    @OnlyIn(SkyBlockIsland.CRIMSON_ISLE)
    fun onChat(event: ChatReceivedEvent.Pre) {
        val content = event.text.trim()
        trophyFishCaughtRegex.match(content, "type", "tier") { (type, tier) ->
            val fishTier = TrophyTier.valueOf(tier)
            val type = TrophyFishType.getByDisplayName(type) ?: return@match

            Text.of {
                append("Caught: ")
                append(type.displayName)
                append(CommonText.SPACE)
                append(fishTier.nameSuffix)
            }.sendWithPrefix()
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
        val caught = mutableMapOf<TrophyTier, Int>()
        event.item.getRawLore().forEach {
            trophyFishDescription.match(it, "tier", "amount") { match ->
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
        TrophyFishStorage.setAmounts(byName, caught)
    }

    @OptIn(SkyBlockPvRequired::class)
    @Subscription
    @OnlyOnSkyBlock
    fun onPv(event: SkyBlockPvOpenedEvent) {
        val obtained = event.member["trophy_fish"].asMap { key, value ->
            if (!value.isJsonPrimitive) null to 0
            else key to value.asInt(0)
        }.filterKeysNotNull()
        var hasLoadedAny = false

        val grouped = obtained.entries.groupBy { group -> TrophyFishType.entries.find { group.key.startsWith(it.internalName, true) } }.filterKeysNotNull()
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
        unlocked.forEach(TrophyFishStorage::setAmounts)
    }

    fun getCaught(type: TrophyFishType): Map<TrophyTier, Int> {
        return TrophyFishStorage.getCaught(type)
    }
}
