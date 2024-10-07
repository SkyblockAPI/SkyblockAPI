package tech.thatgravyboat.skyblockapi.api.profile.storage

import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerStorageStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object StorageAPI {

    private val storageGroup = RegexGroup.INVENTORY.group("storage")

    private val enderchestRegex = storageGroup.create("enderchest", "Ender Chest \\((?<page>\\d+)/\\d+\\)")
    private val backpackRegex = storageGroup.create("backpack", ".* Backpack \\(Slot #(?<page>\\d+)\\)")
    private val riftStorageRegex = storageGroup.create("rift", "Rift Storage \\((?<page>\\d+)/\\d+\\)")

    val enderchests get(): List<PlayerStorageInstance> = PlayerStorageStorage.enderchests
    val backpacks get(): List<PlayerStorageInstance> = PlayerStorageStorage.backpacks
    val riftStorage get(): List<PlayerStorageInstance> = PlayerStorageStorage.riftStorage

    @Subscription
    fun onInventoryLoad(event: ContainerInitializedEvent) {
        val size = McScreen.asMenu?.menu?.slots?.size?.let { it - 36 } ?: return
        enderchestRegex.match(event.title.stripped, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val items = event.itemStacks.take(size).drop(9)
            PlayerStorageStorage.setEnderchest(PlayerStorageInstance(pageId - 1, items.toMutableList()))
        }

        backpackRegex.match(event.title.stripped, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val items = event.itemStacks.take(size).drop(9)
            PlayerStorageStorage.setBackpack(PlayerStorageInstance(pageId - 1, items.toMutableList()))
        }

        riftStorageRegex.match(event.title.stripped, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val items = event.itemStacks.take(size).drop(9)
            PlayerStorageStorage.setRiftStorage(PlayerStorageInstance(pageId - 1, items.toMutableList()))
        }
    }

    @Subscription
    fun onInventoryChange(event: ContainerChangeEvent) {
        val size = McScreen.asMenu?.menu?.slots?.size?.let { it - 36 } ?: return
        if (event.slot < 9 || event.slot >= size) return

        enderchestRegex.match(event.title.stripped, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val instance = PlayerStorageStorage.enderchests.find { it.index == pageId - 1 } ?: return@match
            instance.items[event.slot - 9] = event.item
            PlayerStorageStorage.setEnderchest(instance)
        }

        backpackRegex.match(event.title.stripped, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val instance = PlayerStorageStorage.backpacks.find { it.index == pageId - 1 } ?: return@match
            instance.items[event.slot - 9] = event.item
            PlayerStorageStorage.setBackpack(instance)
        }

        riftStorageRegex.match(event.title.stripped, "page") { (page) ->
            val pageId = page.toIntValue().takeIf { it > 0 } ?: return@match
            val instance = PlayerStorageStorage.riftStorage.find { it.index == pageId - 1 } ?: return@match
            instance.items[event.slot - 9] = event.item
            PlayerStorageStorage.setRiftStorage(instance)
        }
    }
}
