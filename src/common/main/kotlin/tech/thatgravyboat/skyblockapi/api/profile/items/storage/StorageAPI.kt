package tech.thatgravyboat.skyblockapi.api.profile.items.storage

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerStorageStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object StorageAPI {

    private val inventoryGroup = RegexGroup.INVENTORY.group("storage")

    private val enderchestRegex = inventoryGroup.create("enderchest", "Ender Chest \\((?<page>\\d+)/\\d+\\)")
    private val backpackRegex = inventoryGroup.create("backpack", ".* Backpack (?:✦ )?\\(Slot #(?<page>\\d+)\\)")
    private val riftStorageRegex = inventoryGroup.create("rift", "Rift Storage \\((?<page>\\d+)/\\d+\\)")


    /**
     * Note: The index of the storage are stored in the PlayerStorageInstance and the index in the list is not representative of the page.
     */
    val enderchests get(): List<PlayerStorageInstance> = PlayerStorageStorage.enderchests

    /**
     * Note: The index of the storage are stored in the PlayerStorageInstance and the index in the list is not representative of the page.
     */
    val backpacks get(): List<PlayerStorageInstance> = PlayerStorageStorage.backpacks

    /**
     * Note: The index of the storage are stored in the PlayerStorageInstance and the index in the list is not representative of the page.
     */
    val riftStorage get(): List<PlayerStorageInstance> = PlayerStorageStorage.riftStorage

    @Subscription
    fun onInventoryLoad(event: ContainerInitializedEvent) {
        val size = McScreen.asMenu?.menu?.slots?.size?.let { it - 36 } ?: return
        enderchestRegex.match(event.title, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val items = event.itemStacks.take(size).drop(9)
            PlayerStorageStorage.setEnderchest(PlayerStorageInstance(pageId - 1, items.toMutableList()))
        }

        backpackRegex.match(event.title, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val items = event.itemStacks.take(size).drop(9)
            PlayerStorageStorage.setBackpack(PlayerStorageInstance(pageId - 1, items.toMutableList()))
        }

        riftStorageRegex.match(event.title, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val items = event.itemStacks.take(size).drop(9)
            PlayerStorageStorage.setRiftStorage(PlayerStorageInstance(pageId - 1, items.toMutableList()))
        }
    }

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (event.isInPlayerInventory) return
        if (event.isInTopRow) return
        val index = event.slot.index

        enderchestRegex.match(event.title, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val instance = PlayerStorageStorage.enderchests.find { it.index == pageId - 1 } ?: return@match
            instance.items.setAt(index - 9, event.item)
            PlayerStorageStorage.setEnderchest(instance)
        }

        backpackRegex.match(event.title, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val instance = PlayerStorageStorage.backpacks.find { it.index == pageId - 1 } ?: return@match
            instance.items.setAt(index - 9, event.item)
            PlayerStorageStorage.setBackpack(instance)
        }

        riftStorageRegex.match(event.title, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val instance = PlayerStorageStorage.riftStorage.find { it.index == pageId - 1 } ?: return@match
            instance.items.setAt(index - 9, event.item)
            PlayerStorageStorage.setRiftStorage(instance)
        }
    }

    private fun MutableList<ItemStack>.setAt(index: Int, item: ItemStack) {
        if (index < size) {
            this[index] = item
        } else {
            while (size <= index + 1) {
                add(ItemStack.EMPTY)
            }
            this[index] = item
        }
    }
}
