package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktmodules.Module
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.HotfStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemModelTag
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getItemModel
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

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

    private val tokensRegex = inventoryGroup.create(
        "tokens",
        "Tokens of the Forest: (?<tokens>\\d+)",
    )
    //endregion

    val perks: Map<String, HotfPerk>
        get() = HotfStorage.perks
    val unlockedPerks: Map<String, HotfPerk>
        get() = perks.filter { it.value.unlocked }
    val activePerks: Map<String, HotfPerk>
        get() = perks.filter { it.value.unlocked && !it.value.disabled }

    val tokens: Int
        get() = HotfStorage.tokens

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

        if (event.item !in ItemModelTag.HOTF_PERK_ITEMS) return
        val model = event.item.getItemModel()

        var level = 1
        levelRegex.anyMatch(lore, "level") { (_level) ->
            level = _level.toIntValue()
        }
        val disabled = disabledRegex.anyMatch(lore)
        val unlocked = model != Items.MANGROVE_ROOTS && model != Items.PALE_OAK_SAPLING && model != Items.PALE_OAK_BUTTON
        HotfStorage.setPerk(cleanName, HotfPerk(level, unlocked, disabled))
    }
}
