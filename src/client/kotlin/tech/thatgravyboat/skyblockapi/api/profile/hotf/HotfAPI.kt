package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.HotfStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object HotfAPI {

    private val titleRegex = RegexGroup.INVENTORY.group("hotf").create(
        "title",
        "Heart of the Forest",
    )

    private val levelRegex = RegexGroup.INVENTORY.group("hotf").create(
        "level",
        "Level (?<level>\\d+)(?:/\\d+)?",
    )

    private val disabledRegex = RegexGroup.INVENTORY.group("hotf").create(
        "disabled",
        "DISABLED|Click to select!",
    )

    val perks: Map<String, HotfPerk>
        get() = HotfStorage.perks
    val unlockedPerks: Map<String, HotfPerk>
        get() = perks.filter { it.value.unlocked }
    val activePerks: Map<String, HotfPerk>
        get() = perks.filter { it.value.unlocked && !it.value.disabled }

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (!titleRegex.matches(event.title)) return
        if (event.item !in ItemTag.HOTF_PERK_ITEMS) return

        val model = event.item.get(DataComponents.ITEM_MODEL) ?: return

        var level = 1
        levelRegex.anyMatch(event.item.getRawLore(), "level") { (_level) ->
            level = _level.toIntValue()
        }
        val disabled = disabledRegex.anyMatch(event.item.getRawLore())
        val unlocked = !event.item.`is`(Items.COAL) && !event.item.`is`(Items.COAL_BLOCK)
        HotfStorage.setPerk(event.item.cleanName, HotfPerk(level, unlocked, disabled))
    }
}
