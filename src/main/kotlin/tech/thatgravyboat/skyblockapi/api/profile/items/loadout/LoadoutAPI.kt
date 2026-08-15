package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.LoadoutStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.impl.ColoredItems
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.impl.debug.addStringDebug
import tech.thatgravyboat.skyblockapi.utils.container.ContainerRegion
import tech.thatgravyboat.skyblockapi.utils.container.ContentFlow
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import kotlin.math.floor

@Module
data object LoadoutAPI : ItemDebugCategory {

    private inline val storage get() = LoadoutStorage

    private val inventoryGroup = RegexGroup.INVENTORY.group("loadout")
    private val titleRegex = inventoryGroup.create("title", "\\((?<current>\\d+)/(?<max>\\d+)\\) Loadouts")
    private val containerRegion = ContainerRegion(5..7, 1..4)

    @Subscription
    fun onInventoryUpdate(event: InventoryChangeEvent) {
        titleRegex.match(event.title, "current") { (current) ->
            val currentPage = current.toIntValue()
            val slot = containerRegion.getId(event.slot, currentPage, category = fork("Region"), attachable = event) ?: return@match
            event.item.addDebugString { "Slot: $slot" }

            val name = event.item.cleanName
            val locked = event.item.`is`(ColoredItems.RED_DYE)
            event.item.addDebugString { "Locked: $locked" }
        }
    }


}

/*
Helmet: Ancient Warden Helmet ✦
Chestplate: ✿ Ancient Fiery Crimson Chestplate ✪✪✪✪
Leggings: ✿ Ancient Fiery Crimson Leggings ✪✪✪✪
Boots: ✿ Ancient Fiery Crimson Boots ✪✪✪✪

Necklace:  Waxed Bone Necklace ✪✪✪✪✪➊
Cloak:  Waxed Shadow Assassin Cloak ✪✪✪✪✪➋
Belt:  Waxed Adaptive Belt ✪✪✪✪✪➋
Gloves/Bracelet: Waxed Soulweaver Gloves ✪✪✪✪✪➋

Pet: [Lvl 100] Black Cat ✦
HOTM: Heart of the Mountain 2
HOTF: Heart of the Forest 1
Power Stone: Bizarre
Tuning Template Slot: 1

Left-click to equip!
Right-click to edit
*/
