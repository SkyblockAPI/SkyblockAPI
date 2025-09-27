package tech.thatgravyboat.skyblockapi.api.profile.items.sacks

import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.SacksStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ChangedSackItem
import tech.thatgravyboat.skyblockapi.api.events.hypixel.SacksChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.LoadedData
import tech.thatgravyboat.skyblockapi.api.remote.PvLoadingHelper
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.asInt
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findOrNull
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import tech.thatgravyboat.skyblockapi.utils.time.since

@Module
object SacksAPI {
    // [Sacks] +14 items. (Last 5s.)
    // [Sacks] -38 items. (Last 5s.)
    // [Sacks] +38 items, -1 item. (Last 8s.)
    val sackMessageRegex = RegexGroup.CHAT.create(
        "sackapi.message",
        "\\[Sacks] (?:(?<gained>\\+[\\d.,]+) items?,?)?\\s*(?:(?<lost>-[\\d.,]+) items?)?\\.\\s*\\(.*",
    ).toComponentRegex()
    val addedItemsRegex = RegexGroup.CHAT.create("sackapi.changed", " {2}(?<amount>[+-][\\d.,]+) (?<item>.+) \\(")
    val sackTitleRegex = RegexGroup.INVENTORY.create("sackapi.title", ".* Sack")
    val sackAmountRegex = RegexGroup.INVENTORY.create("sackapi.amount", "Stored: (?<amount>[\\d,.]+)/.*")

    val sackItems: Map<String, Int>
        get() = SacksStorage.items.associate { (key, value) -> key to value }

    @Subscription
    @OnlyOnSkyBlock
    fun onChat(event: ChatReceivedEvent.Pre) {
        sackMessageRegex.match(event.component) {
            val gainedHoverComponents = it["gained"]?.hover?.splitLines().orEmpty()
            val lostHoverComponents = it["lost"]?.hover?.splitLines().orEmpty()
            val hoverComponents = gainedHoverComponents + lostHoverComponents

            val changedItems = hoverComponents.mapNotNull {
                addedItemsRegex.findOrNull(it.stripped, "amount", "item") { (amount, item) ->
                    val id = RepoItemsAPI.getItemIdByName(item) ?: return@findOrNull null
                    return@findOrNull id to amount.replace("+", "").toIntValue()
                }
            }

            changedItems.forEach { (item, amount) -> SacksStorage.updateItemValue(item, amount) }
            SacksChangeEvent(changedItems.map { (item, amount) -> ChangedSackItem(item, amount) }).post()
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onInventoryUpdate(event: InventoryChangeEvent) {
        if (event.isInPlayerInventory) return
        if (!sackTitleRegex.matches(event.title)) return
        val item = event.item
        val id = event.item.getData(DataTypes.ID) ?: return

        when (event.title) {
            "Gemstones Sack" -> {
                handleGemstones(item, id)
                return
            }

            "Runes Sack" -> return // No one cares about you
        }

        sackAmountRegex.anyMatch(item.getRawLore(), "amount") { (amount) ->
            SacksStorage.updateItem(id, amount.toIntValue())
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    @OptIn(SkyBlockPvRequired::class)
    private fun SkyBlockPvOpenedEvent.updateSacks() {
        val sacks = member.getPath("inventory.sacks_counts") as? JsonObject ?: return
        sacks.entrySet().forEach { (itemId, amount) ->
            val amount = amount.asInt(0).takeUnless { it <= 0 } ?: return@forEach
            val entry = SacksStorage.items.find { it.id == itemId }
            val previousAmount = entry?.amount ?: 0
            if (amount == previousAmount) {
                return@forEach
            }
            val isInvalid = entry?.lastUpdated?.since()?.let { it < PvLoadingHelper.timeToLive } == true
            if (isInvalid) {
                return@forEach
            }

            SacksStorage.updateItem(itemId, amount)
            PvLoadingHelper.markLoaded(LoadedData.SACKS)
        }
    }

    private fun handleGemstones(item: ItemStack, id: String) {
        listOf("Rough", "Flawed", "Fine").forEach { name ->
            Regex(" $name: (?<amount>[\\d,.]+) .*").anyMatch(item.getRawLore(), "amount") { (amount) ->
                val actualId = id.replace("ROUGH", name.uppercase())
                SacksStorage.updateItem(actualId, amount.toIntValue())
            }
        }
    }
}
