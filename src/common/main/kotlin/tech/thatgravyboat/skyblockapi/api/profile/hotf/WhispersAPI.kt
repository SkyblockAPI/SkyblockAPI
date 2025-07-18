package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.HotfStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

private const val MAIN_SLOT = 49
private const val RESET_SLOT = 52

@Module
object WhispersAPI {

    //region Regex
    private val inventoryGroup = RegexGroup.INVENTORY.group("whispers")

    private val titleRegex = inventoryGroup.create(
        "title",
        "Heart of the Forest",
    )
    private val whispersItemRegex = inventoryGroup.create(
        "forest.current",
        "Forest Whispers: (?<amount>[\\d,.]+)",
    )
    private val whispersSpentItemRegex = inventoryGroup.create(
        "forest.spent",
        "\\s*-\\s*(?<amount>[\\d,.]+) Forest Whispers",
    )
    private val forestWhispersRegex = RegexGroup.TABLIST_WIDGET.group("hotf").create(
        "whispers",
        "Forest Whispers: (?<amount>[\\w,.]+)",
    )
    //endregion

    val forest: Long
        get() = HotfStorage.forest

    val forestTotal: Long
        get() = HotfStorage.forestTotal


    @Subscription
    @OnlyWidget(TabWidget.FOREST_WHISPERS)
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        forestWhispersRegex.anyMatch(event.new, "amount") { (amount) ->
            val amount = amount.parseFormattedLong()
            val diff = amount - HotfStorage.forest
            HotfStorage.forest = amount
            if (diff > 0) HotfStorage.forestTotal += diff
        }
    }

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (!titleRegex.matches(event.title)) return
        val mainItem = event.itemStacks.getOrNull(MAIN_SLOT) ?: return
        val resetItem = event.itemStacks.getOrNull(RESET_SLOT)

        var whispersTotal = 0L

        val mainLore = mainItem.getRawLore()
        for (line in mainLore) {
            whispersItemRegex.match(line, "amount") { (amount) ->
                HotfStorage.forest = amount.toLongValue()
                whispersTotal += this.forest
            }
        }

        if (resetItem != null) {
            whispersSpentItemRegex.anyMatch(resetItem.getRawLore(), "amount") { (amount) ->
                whispersTotal += amount.toLongValue()
            }
        }

        HotfStorage.forestTotal = whispersTotal
    }

}
