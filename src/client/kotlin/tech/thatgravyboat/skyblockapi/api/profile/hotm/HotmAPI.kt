package tech.thatgravyboat.skyblockapi.api.profile.hotm

import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.HotmStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object HotmAPI {

    private val levelRegex = RegexGroup.INVENTORY.group("hotm").create(
        "level",
        "Level (?<level>\\d+)(?:/\\d+)?",
    )

    private val disabledRegex = RegexGroup.INVENTORY.group("hotm").create(
        "disabled",
        "DISABLED|Click to select!",
    )

    private var holdingBlueOmelette = false

    val perks: Map<String, HotmPerk>
        get() = HotmStorage.perks
    val unlockedPerks: Map<String, HotmPerk>
        get() = perks.filter { it.value.unlocked }
    val activePerks: Map<String, HotmPerk>
        get() = perks.filter { it.value.unlocked && !it.value.disabled }

    @Subscription
    fun onInventoryOpen(event: ContainerInitializedEvent) {
        holdingBlueOmelette = McPlayer.self?.mainHandItem?.getData(DataTypes.UPGRADE_MODULE).equals("GOBLIN_OMELETTE_BLUE_CHEESE", true)
    }

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (event.title != "Heart of the Mountain") return
        if (event.item !in ItemTag.HOTM_PERK_ITEMS) return

        var level = 1
        levelRegex.anyMatch(event.item.getRawLore(), "level") { (_level) ->
            level = _level.toIntValue()
        }
        if (holdingBlueOmelette) level = (level - 1).coerceAtLeast(1)
        val disabled = disabledRegex.anyMatch(event.item.getRawLore())
        val unlocked = !event.item.`is`(Items.COAL) && !event.item.`is`(Items.COAL_BLOCK)
        HotmStorage.setPerk(event.item.cleanName, HotmPerk(level, unlocked, disabled))
    }
}
