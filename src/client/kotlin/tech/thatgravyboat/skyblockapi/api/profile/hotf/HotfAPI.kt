package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktmodules.Module
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.HotfStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemModelTag
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

private const val MAIN_SLOT = 49
private const val RESET_SLOT = 52

@Module
object HotfAPI {

    //region Regex
    private val inventoryGroup = RegexGroup.INVENTORY.group("hotf")

    private val titleRegex = inventoryGroup.create(
        "title",
        "Heart of the Forest",
    )

    private val levelRegex = inventoryGroup.create(
        "level",
        "Level (?<level>\\d+)(?:/\\d+)?",
    )

    private val disabledRegex = inventoryGroup.create(
        "disabled",
        "DISABLED|Click to select!",
    )

    private val mainItem = inventoryGroup.create(
        "mainitem",
        "Heart of the Forest",
    )

    private val whispersItemRegex = inventoryGroup.create(
        "whispers.current",
        "Forest Whispers: (?<amount>[\\d,.]+)",
    )
    private val whispersSpentItemRegex = inventoryGroup.create(
        "whispers.spent",
        "\\s*-\\s*(?<amount>[\\d,.]+) Forest Whispers",
    )

    private val tokensRegex = inventoryGroup.create(
        "tokens",
        "Tokens of the Forest: (?<tokens>\\d+)",
    )
    private val forestWhispersRegex = RegexGroup.TABLIST_WIDGET.group("hotf").create(
        "whispers",
        "Forest Whispers: (?<amount>[\\w,.]+)",
    )
    //endregion

    val perks: Map<String, HotfPerk>
        get() = HotfStorage.perks
    val unlockedPerks: Map<String, HotfPerk>
        get() = perks.filter { it.value.unlocked }
    val activePerks: Map<String, HotfPerk>
        get() = perks.filter { it.value.unlocked && !it.value.disabled }

    val whispers: Long
        get() = HotfStorage.whispers

    val whispersTotal: Long
        get() = HotfStorage.whispersTotal

    val tokens: Int
        get() = HotfStorage.tokens

    @Subscription
    @OnlyWidget(TabWidget.FOREST_WHISPERS)
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        forestWhispersRegex.anyMatch(event.new, "amount") { (amount) ->
            val amount = amount.parseFormattedLong()
            val diff = amount - HotfStorage.whispers
            HotfStorage.whispers = amount
            if (diff > 0) HotfStorage.whispersTotal += diff
        }
    }

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (!titleRegex.matches(event.title)) return

        val lore = event.item.getRawLore()
        val cleanName = event.item.cleanName

        if (mainItem.matches(cleanName)) {
            tokensRegex.anyMatch(lore, "tokens") { (tokens) ->
                HotfStorage.tokens = tokens.toIntValue()
            }
            return
        }

        if (event.item in ItemModelTag.HOTF_PERK_ITEMS) {
            val model = event.item.getItemModel()

            var level = 1
            levelRegex.anyMatch(lore, "level") { (_level) ->
                level = _level.toIntValue()
            }
            val disabled = disabledRegex.anyMatch(lore)
            val unlocked = model != Items.MANGROVE_ROOTS && model != Items.PALE_OAK_SAPLING && model != Items.PALE_OAK_BUTTON
            HotfStorage.setPerk(cleanName, HotfPerk(level, unlocked, disabled))
            return
        }

        // Whispers and Total Whispers
        val mainItem = event.itemStacks.getOrNull(MAIN_SLOT) ?: return
        val resetItem = event.itemStacks.getOrNull(RESET_SLOT)

        var whispersTotal = 0L

        val mainLore = mainItem.getRawLore()
        for (line in mainLore) {
            whispersItemRegex.match(line, "amount") { (amount) ->
                HotfStorage.whispers = amount.toLongValue()
                whispersTotal += this.whispers
            }
        }

        if (resetItem != null) {
            whispersSpentItemRegex.anyMatch(resetItem.getRawLore(), "amount") { (amount) ->
                whispersTotal += amount.toLongValue()
            }
        }

        HotfStorage.whispersTotal = whispersTotal
    }
}
