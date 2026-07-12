package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.TrophyFishStorage
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
import kotlin.math.max

@Module
object TrophyFishingAPI {

    private val chatGroup = RegexGroup.CHAT.group("trophy_api")
    private val inventoryGroup = RegexGroup.INVENTORY.group("trophy_api")

    private val singleTrophyFishCaughtRegex = chatGroup.create(
        "caught",
        ". TROPHY FISH! You caught an? (?<type>.+?) (?<tier>${TrophyFishTier.entries.joinToString("|", transform = { it.name })})!",
    )

    private val multiTrophyFishCaughtRegex = chatGroup.create(
        "caught",
        ". TROPHY FISH! You caught (?<type>.+?) (?<tier>${TrophyFishTier.entries.joinToString("|", transform = { it.name })}) x(?<amount>\\d+)!",
    )

    private val trophyFishDescription = inventoryGroup.create(
        "description",
        "(?<tier>Diamond|Silver|Gold|Bronze) \\S+(?: \\((?<amount>\\d+)\\))?",
    )

    @Subscription
    @OnlyIn(SkyBlockIsland.CRIMSON_ISLE)
    fun onChat(event: ChatReceivedEvent.Pre) {
        val content = event.text.trim()
        when {
            singleTrophyFishCaughtRegex.matches(content) -> {
                singleTrophyFishCaughtRegex.match(content, "type", "tier") { (type, tier) ->
                    val fishTier = TrophyFishTier.valueOf(tier)
                    val type = TrophyFishType.getByDisplayName(type) ?: return@match

                    TrophyFishStorage.addCaught(type, fishTier)
                    TrophyFishCaughtEvent(type, fishTier).post()
                }
            }
            multiTrophyFishCaughtRegex.matches(content) -> {
                multiTrophyFishCaughtRegex.match(content, "type", "tier", "amount") { (type, tier, amount) ->
                    val fishTier = TrophyFishTier.valueOf(tier)
                    val type = TrophyFishType.getByDisplayName(type) ?: return@match
                    val amount = amount.toIntOrNull() ?: return@match

                    TrophyFishStorage.addCaught(type, fishTier)
                    TrophyFishCaughtEvent(type, fishTier, amount).post()
                }
            }
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
                val tier = TrophyFishTier.entries.find { value.key.endsWith(it.name, true) }
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

    fun getCaught(type: TrophyFishType): Map<TrophyFishTier, Int> {
        return TrophyFishStorage.getCaught(type)
    }
}
