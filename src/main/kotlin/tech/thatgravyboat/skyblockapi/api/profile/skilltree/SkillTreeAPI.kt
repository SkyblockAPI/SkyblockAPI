package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.SkillTreeStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.StringMatch
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.value
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTagKey
import tech.thatgravyboat.skyblockapi.utils.container.ContainerRegion
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen

public abstract class SkillTreeAPI<
    Perk : SkillTreePerk,
    Self : SkillTreeAPI<Perk, Self>
> internal constructor(
    public val name: String,
    private val perkItems: ItemTagKey,
    private val storage: SkillTreeStorage<Perk, out SkillTreeData<Perk>>,
    identifier: String,
    public val type: SkillTreeType<SkillTreeAPI<Perk, Self>>,
) : ItemDebugCategory {
    protected val inventoryGroup: RegexGroup = RegexGroup.INVENTORY.group(name)
    protected val presetGroup: RegexGroup = inventoryGroup.group("preset")

    internal open val titleRegex: Regex = inventoryGroup.create("title", "Heart of the $identifier")
    protected open val levelRegex: Regex = inventoryGroup.create("level", "Level (?<level>\\d+)(?:/\\d+)?")
    protected open val disabledRegex: Regex = inventoryGroup.create("disabled", "DISABLED|Click to select!")
    //? if < 26.4
    protected open val mainItemRegex: Regex = inventoryGroup.create("mainitem", "Heart of the $identifier")
    protected open val tokensRegex: Regex = inventoryGroup.create("tokens", "Tokens? of the $identifier: (?<tokens>\\d+)")
    protected open val spentTokensRegex: Regex = inventoryGroup.create("tokens.spent", "\\s*-\\s*(?<tokens>[\\d,.]+) Tokens? of the $identifier")
    protected open val tierRegex: Regex = inventoryGroup.create("tier", "Tier (?<tier>\\d+)")
    protected open val tierUnlockedRegex: Regex = inventoryGroup.create("tier.unlocked", "UNLOCKED")

    protected open val currentPresetRegex: Regex = presetGroup.create("current", "Current: (?<preset>.+)")
    internal open val presetsTitleRegex: Regex = presetGroup.create("title", "Heart of the $identifier Slot")
    protected open val lockedLoadoutRegex: Regex = presetGroup.create("loadout", "LOCKED")

    protected open val presetsRegion: ContainerRegion = ContainerRegion(width = 5, height = 1, startRow = 1, startColumn = 2)

    public open val perks: Map<String, Perk>
        get() = storage.perks
    public val unlockedPerks: Map<String, Perk>
        get() = perks.filter { it.value.unlocked }
    public val activePerks: Map<String, Perk>
        get() = perks.filter { it.value.unlocked && !it.value.disabled }

    public open val tokens: Int
        get() = storage.tokens

    public open val tier: Int
        get() = storage.tier

    internal open fun getCurrentPreset(lore: List<String>): String? {
        return currentPresetRegex.findGroup(lore, "preset")
    }

    context(event: InventoryChangeEvent)
    protected inline fun checkItem(index: Int, block: ItemStack.() -> Unit): Boolean {
        val item = event.itemStacks.getOrNull(index) ?: return false
        block(item)
        return true
    }

    context(event: InventoryChangeEvent)
    protected open fun onMainInventory() {
        val itemStacks = event.itemStacks
        if (itemStacks.last().isEmpty) return // inventory not fully loaded

        val validPresetSlot = checkItem(PRESET_SLOT) {
            val preset = getCurrentPreset(getRawLore()) ?: return
            storage.currentPreset = preset
        }
        if (!validPresetSlot) return // preset slot should ALWAYS exist, if not something is wrong

        var maxTokens = 0
        checkItem(MAIN_SLOT) {
            tokensRegex.anyMatch(getRawLore(), "tokens") { (tokens) ->
                maxTokens += tokens.toIntValue()
            }
        }
        checkItem(RESET_SLOT) {
            spentTokensRegex.anyMatch(getRawLore(), "tokens") { (tokens) ->
                val spentTokens = tokens.toIntValue()
                storage.spentTokens = spentTokens
                maxTokens += spentTokens
            }
        }

        if (maxTokens != 0) storage.maxTokens = maxTokens

        for (item in event.columnItems(0)) {
            if (item !in ItemTag.GLASS_PANES) continue
            val tier = tierRegex.findGroup(item.cleanName, "tier")?.toIntValue() ?: continue
            val unlocked = tierUnlockedRegex.matches(item.getRawLore().last())
            if (unlocked) {
                storage.setMinTier(tier)
                break
            }
        }

        itemStacks.forEach { item ->
            if (item !in perkItems) return@forEach
            val lore = item.getRawLore()
            val cleanName = item.cleanName
            var level = levelRegex.findGroup(lore, "level")?.toIntValue() ?: 1
            level = adjustLevel(level)

            val disabled = disabledRegex.anyMatch(lore)
            val unlocked = isUnlocked(event.item)
            storage.setPerk(cleanName, createPerk(level, unlocked, disabled))
        }
    }

    internal open fun getLoadoutIdAndName(event: InventoryChangeEvent, debugCategory: ItemDebugCategory): Pair<Int, String>? {
        if (event.isSkyBlockFiller || !event.isInMainPart) return null
        val id = presetsRegion.getId(event.slot, category = debugCategory, attachable = event) ?: return null
        val isLocked = lockedLoadoutRegex.anyMatch(event.item.getRawLore())
        if (isLocked) return null
        val loadoutName = event.item.cleanName
        return id to loadoutName
    }

    context(event: InventoryChangeEvent)
    protected open fun onPresetsInventory() {
        val (id, loadoutName) = getLoadoutIdAndName(event, this.fork("Presets Region")) ?: return
        storage.updateLoadout(id, loadoutName)
    }

    @Subscription(inherited = true)
    context(event: InventoryChangeEvent)
    protected open fun onInventoryChange() {
        matchWhen(event.title) {
            case(titleRegex) {
                onMainInventory()
            }
            case(presetsTitleRegex) {
                onPresetsInventory()
            }
        }
    }

    @Subscription(inherited = true)
    context(event: LoadoutChangeEvent)
    private fun onLoadoutChange() {
        val newSlot = event.new?.getLoadoutMatch().value() ?: return
        storage.currentPreset = newSlot
    }

    protected abstract fun LoadoutSlot.getLoadoutMatch(): StringMatch?
    protected open fun adjustLevel(level: Int): Int = level
    protected abstract fun isUnlocked(item: ItemStack): Boolean
    protected abstract fun createPerk(level: Int, unlocked: Boolean, disabled: Boolean): Perk

    internal companion object {
        internal const val MAIN_SLOT = 49
        internal const val PRESET_SLOT = 47
        internal const val RESET_SLOT = 52
    }
}
