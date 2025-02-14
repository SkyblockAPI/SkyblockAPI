package tech.thatgravyboat.skyblockapi.api.profile.sacks

import tech.thatgravyboat.skyblockapi.api.data.stored.SacksStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ChangedSackItem
import tech.thatgravyboat.skyblockapi.api.events.hypixel.SacksChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines

@Module
object SacksAPI {
    val sackMessageRegex = ComponentRegex("\\[Sacks\\] (?<amount>[+-][\\d.,]+) items?\\.+ \\(.*")
    val addedItemsRegex = RegexGroup.CHAT.create("sackapi.changed", " {2}(?<amount>[+-][\\d.,]+) (?<item>.+) \\(.*")
    val sackTitleRegex = RegexGroup.INVENTORY.create("sackapi.title", ".* Sack")
    val sackAmountRegex = RegexGroup.INVENTORY.create("sackapi.amount", "Stored: (?<amount>[\\d,.]+)/.*")

    val sackItems: Map<String, Int>
        get() = SacksStorage.items

    @Subscription
    @OnlyOnSkyBlock
    fun onChat(event: ChatReceivedEvent) {
        sackMessageRegex.match(event.component, "amount") { (amountComponent) ->
            val hoverComponents = amountComponent.copy().hover?.splitLines() ?: emptyList()

            val changedItems = hoverComponents.mapNotNull {
                var amount: Int? = null
                var item: String? = null
                addedItemsRegex.match(it.stripped, "amount", "item") { (_amount, _item) ->
                    amount = _amount.replace("+", "").toIntValue()
                    item = _item
                }
                if (item != null && amount != null) item to amount
                else null
            }

            changedItems.forEach { (item, amount) -> SacksStorage.updateItemValue(item, amount) }
            SacksChangeEvent(changedItems.map { (item, amount) -> ChangedSackItem(item, amount) }).post()
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onInventoryUpdate(event: ContainerChangeEvent) {
        if (!sackTitleRegex.matches(event.title)) return
        val item = event.item

        sackAmountRegex.anyMatch(item.getRawLore(), "amount") { (amount) ->
            SacksStorage.updateItem(item.hoverName.stripped, amount.toIntValue())
        }
    }

}
