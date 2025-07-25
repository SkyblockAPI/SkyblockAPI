package tech.thatgravyboat.skyblockapi.api.profile.items.storage

import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerStorageStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.LoadedData
import tech.thatgravyboat.skyblockapi.api.remote.PvLoadingHelper
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.parseInvData
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.time.since
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

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

    fun Duration.toReadableTime(biggestUnit: DurationUnit = DurationUnit.DAYS, maxUnits: Int = 2, allowMs: Boolean = false): String {
        val units = listOfNotNull(
            DurationUnit.DAYS to this.inWholeDays,
            DurationUnit.HOURS to this.inWholeHours % 24,
            DurationUnit.MINUTES to this.inWholeMinutes % 60,
            DurationUnit.SECONDS to this.inWholeSeconds % 60,
            (DurationUnit.MILLISECONDS to this.inWholeMilliseconds % 1000).takeIf { allowMs },
        )

        val unitNames = listOfNotNull(
            DurationUnit.DAYS to "d",
            DurationUnit.HOURS to "h",
            DurationUnit.MINUTES to "min",
            DurationUnit.SECONDS to "s",
            (DurationUnit.MILLISECONDS to "ms").takeIf { allowMs },
        ).toMap()

        val filteredUnits = units.dropWhile { it.first != biggestUnit }
            .filter { it.second > 0 }
            .take(maxUnits)

        return filteredUnits.joinToString(", ") { (unit, value) ->
            "$value${unitNames[unit]}"
        }.ifEmpty { "0 seconds" }
    }

    @Subscription
    @OnlyOnSkyBlock
    @OptIn(SkyBlockPvRequired::class)
    private fun SkyBlockPvOpenedEvent.parseEnderChest() {
        val rawEnderchestData = member.getPath("inventory.ender_chest_contents") as? JsonObject ?: return
        val enderchestData = rawEnderchestData.parseInvData().takeUnless { it.isEmpty() } ?: return
        val enderchest = enderchestData.chunked(45)

        enderchest.forEachIndexed { index, items ->
            val lastUpdate = enderchests.find { it.index == index }?.lastUpdated
            val isInvalid = lastUpdate?.since()?.let { it < 15.minutes } == true
            if (isInvalid) {
                return@forEachIndexed
            }

            PvLoadingHelper.markLoaded(LoadedData.ENDERCHEST)
            PlayerStorageStorage.setEnderchest(PlayerStorageInstance(index, items.toMutableList()))
        }
    }


    @Subscription
    @OnlyOnSkyBlock
    @OptIn(SkyBlockPvRequired::class)
    private fun SkyBlockPvOpenedEvent.parseBackPacks() {
        val rawBackPackData = member.getPath("inventory.backpack_contents") as? JsonObject ?: return
        rawBackPackData.entrySet().forEach { (index, json) ->
            val index = index.toIntValue()
            val lastUpdate = backpacks.find { it.index == index }?.lastUpdated
            val isInvalid = lastUpdate?.since()?.let { it < 15.minutes } == true
            if (isInvalid) {
                return@forEach
            }
            val data = (json as? JsonObject)?.parseInvData() ?: return@forEach

            PvLoadingHelper.markLoaded(LoadedData.BACKPACK)
            PlayerStorageStorage.setBackpack(PlayerStorageInstance(index, data.toMutableList()))
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
