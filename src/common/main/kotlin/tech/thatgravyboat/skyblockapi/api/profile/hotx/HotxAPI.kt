package tech.thatgravyboat.skyblockapi.api.profile.hotx

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.HotxStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTagKey
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

abstract class HotxAPI<Data : HotxData<Perk>, Perk : HotxPerk> internal constructor(
    regexGroup: String,
    private val perkItems: ItemTagKey,
    private val storage: HotxStorage<Data, Perk>,
    private val identifier: String,
) {
    private val inventoryGroup = RegexGroup.INVENTORY.group(regexGroup)

    protected open val titleRegex = inventoryGroup.create("title", "Heart of the $identifier")
    protected open val levelRegex = inventoryGroup.create("level", "Level (?<level>\\d+)(?:/\\d+)?")
    protected open val disabledRegex = inventoryGroup.create("disabled", "DISABLED|Click to select!")
    protected open val mainItemRegex = inventoryGroup.create("mainitem", "Heart of the $identifier")
    protected open val tokensRegex = inventoryGroup.create("tokens", "Tokens of the $identifier: (?<tokens>\\d+)")

    open val perks: Map<String, HotxPerk>
        get() = storage.perks
    val unlockedPerks: Map<String, HotxPerk>
        get() = perks.filter { it.value.unlocked }
    val activePerks: Map<String, HotxPerk>
        get() = perks.filter { it.value.unlocked && !it.value.disabled }

    open val tokens: Int
        get() = storage.tokens

    @Subscription(inherited = true)
    open fun onInventoryChange(event: InventoryChangeEvent) {
        if (!titleRegex.matches(event.title)) return

        val lore = event.item.getRawLore()
        val cleanName = event.item.cleanName

        if (mainItemRegex.matches(cleanName)) {
            tokensRegex.anyMatch(lore, "tokens") { (tokens) ->
                storage.tokens = tokens.toIntValue()
            }
            return
        }

        if (event.item !in perkItems) return

        var level = 1
        levelRegex.anyMatch(lore, "level") { (_level) ->
            level = _level.toIntValue()
        }
        level = adjustLevel(level)

        val disabled = disabledRegex.anyMatch(lore)
        val unlocked = isUnlocked(event.item)
        storage.setPerk(cleanName, createPerk(level, unlocked, disabled))
    }

    protected open fun adjustLevel(level: Int): Int = level
    protected open fun isUnlocked(item: ItemStack): Boolean = true
    protected abstract fun createPerk(level: Int, unlocked: Boolean, disabled: Boolean): Perk
}
