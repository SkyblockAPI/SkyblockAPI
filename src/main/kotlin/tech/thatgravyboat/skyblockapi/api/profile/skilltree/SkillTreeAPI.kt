package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.SkillTreeStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTagKey
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup

public abstract class SkillTreeAPI<Data : SkillTreeData<Perk>, Perk : SkillTreePerk, Self : SkillTreeAPI<Data, Perk, Self>> internal constructor(
    public val name: String,
    private val perkItems: ItemTagKey,
    private val storage: SkillTreeStorage<Data, Perk>,
    identifier: String,
    public val type: SkillTreeType<Self>,
) {
    protected val inventoryGroup: RegexGroup = RegexGroup.INVENTORY.group(name)

    internal open val titleRegex = inventoryGroup.create("title", "Heart of the $identifier")
    protected open val levelRegex: Regex = inventoryGroup.create("level", "Level (?<level>\\d+)(?:/\\d+)?")
    protected open val disabledRegex: Regex = inventoryGroup.create("disabled", "DISABLED|Click to select!")
    protected open val mainItemRegex: Regex = inventoryGroup.create("mainitem", "Heart of the $identifier")
    protected open val tokensRegex: Regex = inventoryGroup.create("tokens", "Tokens? of the $identifier: (?<tokens>\\d+)")
    protected open val tierRegex: Regex = inventoryGroup.create("tier", "Tier (?<tier>\\d+)")
    protected open val tierUnlockedRegex: Regex = inventoryGroup.create("tier.unlocked", "UNLOCKED")

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

    @Subscription(inherited = true)
    protected open fun onInventoryChange(event: InventoryChangeEvent) {
        if (!titleRegex.matches(event.title)) return

        val lore = event.item.getRawLore()
        val cleanName = event.item.cleanName

        if (lore.isEmpty()) return

        if (event.isOnLeftColumn) {
            if (event.item !in ItemTag.GLASS_PANES) return
            val tier = tierRegex.findGroup(cleanName, "tier")?.toIntValue() ?: return
            val unlocked = tierUnlockedRegex.matches(lore.last())
            if (unlocked) storage.setMinTier(tier)
            return
        }

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
    protected abstract fun isUnlocked(item: ItemStack): Boolean
    protected abstract fun createPerk(level: Int, unlocked: Boolean, disabled: Boolean): Perk
}
