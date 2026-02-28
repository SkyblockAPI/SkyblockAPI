package tech.thatgravyboat.skyblockapi.api.profile.items.museum

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.MuseumStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvMuseumOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.SlotClickEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.remote.LoadedData
import tech.thatgravyboat.skyblockapi.api.remote.PvLoadingHelper
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.museum.MuseumData
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.underlined
import java.util.concurrent.CompletableFuture

/** Currently doesn't support special items. */
@Module
object MuseumAPI {

    //region Regex
    private val inventoryGroup = RegexGroup.INVENTORY.group("museum")

    private val mainMuseumTitleRegex = inventoryGroup.create(
        "main.title",
        "Your Museum",
    )
    private val inventoryTitleRegex = inventoryGroup.create(
        "title",
        "Museum ➜ .+$",
    )
    private val donateTitleRegex = inventoryGroup.create(
        "donate.title",
        "Confirm Donation",
    )
    private val confirmDonateItem = inventoryGroup.create(
        "donate.item",
        "Confirm Donation",
    )
    private val museumRewardsItem = inventoryGroup.create(
        "rewards.item",
        "Museum Rewards",
    )
    private val museumMilestoneRegex = inventoryGroup.create(
        "milestone",
        "Milestone: (?<milestone>\\d+)/\\d+",
    )
    private val multipleItemsStoredRegex = inventoryGroup.create(
        "stored.multiple.item",
        "^Right-click to view armor set!$"
    )
    private val multipleItemsNotStoredRegex = inventoryGroup.create(
        "donate.multiple.item",
        "^Click on an armor piece in your$"
    )

    //endregion

    val milestone: Int get() = MuseumStorage.milestone

    fun getAllItems(): List<ItemStack> = MuseumStorage.getAllItems()
    fun getItemsOnCategory(category: MuseumCategory): List<ItemStack> = MuseumStorage.getItemsOnCategory(category)
    fun getItemsWithCategory(): Map<MuseumCategory, List<ItemStack>> = MuseumStorage.getItemsWithCategory()

    /** Returns `true` if the item can be donated to museum. Ignores special items. */
    fun isMuseumItem(id: SkyBlockId): Boolean = MuseumData.isMuseumItem(id)

    /** Returns `true` if the item has been donated to museum. Ignores special items. */
    fun isDonated(id: SkyBlockId): Boolean = internalIsDonated(id, mutableSetOf())

    /** Returns `true` if the item has been donated to museum and is stored in it. Ignores special items. */
    fun isStoredInMuseum(id: SkyBlockId): Boolean = MuseumStorage.isItemStored(id)

    /** Returns the item with id [id] stored in the museum, or null if it's not stored. */
    fun getItemInMuseum(id: SkyBlockId): ItemStack? = MuseumStorage.getItemStored(id)

    private fun internalIsDonated(id: SkyBlockId, checkedIds: MutableSet<SkyBlockId>): Boolean {
        if (!checkedIds.add(id)) return false

        if (MuseumStorage.hasDonatedItem(id)) return true

        val itemData = ItemData.getItemData(id)?.museumData ?: return false

        return itemData.parents.values.any { parent ->
            val armorSet = MuseumData.getArmorSetFromId(parent)

            // if the parent is an armor set, check that all items in that armor set have been donated
            if (armorSet != null) {
                val hasDonatedAllArmorSet = armorSet.all {
                    val id = SkyBlockId.item(it)
                    internalIsDonated(id, checkedIds)
                }
                if (hasDonatedAllArmorSet) return true
            }

            // if the parent isn't an armor set (or as a fallback if it is), check the parent as an item
            val id = SkyBlockId.item(parent)
            internalIsDonated(id, checkedIds)
        }
    }

    private fun ItemStack.isNotDonated(): Boolean = this in Items.GRAY_DYE
    private fun ItemStack.isNotStored(): Boolean = this in Items.LIME_DYE

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB)
    fun onSlotClick(event: SlotClickEvent) {
        if (event.isInPlayerInventory) return
        if (!donateTitleRegex.match(event.title)) return
        val item = event.item
        if (item !in Items.LIME_TERRACOTTA) return
        if (!confirmDonateItem.match(item.cleanName)) return
        val items = event.slots.subList(0, event.slot.index - 1)
            .map { it.item }
            .associateByNotNull { it.getSkyBlockId() }
            .filterValues { !it.isSkyblockFiller() }

        if (items.isEmpty()) return

        items.forEach { (id, item) ->
            val data = ItemData.getItemData(id)?.museumData ?: return@forEach
            MuseumStorage.addItem(data.category, id, item)
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB)
    fun onInventoryOpen(event: ContainerInitializedEvent) {
        val items = event.containerItems
        if (!inventoryTitleRegex.contains(event.title)) return
        val filtered = items.filterNot { it.isSkyblockFiller() }
        if (filtered.isEmpty()) return

        for (item in filtered) {
            when {
                item.isNotDonated() -> {
                    val isMultipleItems = multipleItemsNotStoredRegex.anyFound(item.getRawLore())
                    if (isMultipleItems) {
                        val items = MuseumData.getArmorSetFromName(item.cleanName) ?: continue
                        val ids = items.map(SkyBlockId::item)
                        ids.forEach(MuseumStorage::deleteItem)
                        continue
                    }
                    val id = RepoItemsAPI.getItemIdByName(item.cleanName) ?: continue
                    val sbId = SkyBlockId.item(id)
                    MuseumStorage.deleteItem(sbId)
                }
                item.isNotStored() -> {
                    val isMultipleItems = multipleItemsNotStoredRegex.anyFound(item.getRawLore())
                    if (isMultipleItems) {
                        val items = MuseumData.getArmorSetFromName(item.cleanName) ?: continue
                        val ids = items.map(SkyBlockId::item)
                        ids.forEach { id ->
                            val category = getCategory(id) ?: return@forEach
                            MuseumStorage.addNotStoredItem(category, id)
                        }
                        continue
                    }
                    val id = RepoItemsAPI.getItemIdByName(item.cleanName)?.let(SkyBlockId::item) ?: continue
                    val category = getCategory(id) ?: continue
                    MuseumStorage.addNotStoredItem(category, id)
                }
                else -> {
                    val isMultipleItems = multipleItemsStoredRegex.anyFound(item.getRawLore())
                    if (isMultipleItems) {
                        val items = MuseumData.getArmorSetFromName(item.cleanName) ?: continue
                        val ids = items.map(SkyBlockId::item)
                        if (ids.all(::isDonated)) continue // if it's already counted as donated, ignore
                        // store them as at least not stored, as we cant actually know the items here
                        ids.forEach { id ->
                            val category = getCategory(id) ?: return@forEach
                            MuseumStorage.addNotStoredItem(category, id)
                        }
                        continue
                    }

                    val id = item.getSkyBlockId() ?: continue
                    val category = getCategory(id) ?: continue
                    MuseumStorage.addItem(category, id, item)
                }
            }
        }
    }

    // gets museum category of said skyblock id, ignoring special items
    private fun getCategory(id: SkyBlockId): MuseumCategory? {
        return ItemData.getItemData(id)?.museumData?.category?.takeUnless(MuseumCategory::isSpecial)
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB)
    fun onInventoryUpdate(event: InventoryChangeEvent) {
        if (!mainMuseumTitleRegex.match(event.title)) return
        if (!museumRewardsItem.match(event.item.cleanName)) return

        museumMilestoneRegex.anyMatch(event.item.getRawLore(), "milestone") { (milestoneStr) ->
            MuseumStorage.setMilestone(milestoneStr.toIntOrNull() ?: return@anyMatch)
        }
    }

    @OptIn(SkyBlockPvRequired::class)
    @Subscription
    fun onPvOpen(event: SkyBlockPvMuseumOpenedEvent) {
        CompletableFuture.runAsync {
            MuseumStorage.reset()
            PvLoadingHelper.markLoaded(LoadedData.MUSEUM)
            for (entry in event.entries) {
                val (museumId, stacks) = entry
                val isArmor = MuseumData.isArmorSet(museumId)
                val map = buildMap {
                    stacks@ for (stack in stacks) {
                        val item = stack.value
                        val id = item.getSkyBlockId() ?: continue@stacks
                        put(id, item)
                    }
                }
                if (map.isNotEmpty()) {
                    map.forEach { (id, item) ->
                        val category = getCategory(id) ?: return@forEach
                        MuseumStorage.addItem(category, id, item)
                    }
                } else if (isArmor) {
                    val items = MuseumData.getArmorSetFromId(museumId)?.map(SkyBlockId::item) ?: return@runAsync
                    items.forEach { id ->
                        val category = getCategory(id) ?: return@forEach
                        MuseumStorage.addNotStoredItem(category, id)
                    }
                } else {
                    val id = SkyBlockId.item(museumId)
                    val category = getCategory(id) ?: return@runAsync
                    MuseumStorage.addNotStoredItem(category, id)
                }
            }
        }
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("sbapi museum") {
            thenCallback("reset") {
                MuseumStorage.reset()
                Text.sendDebug("Museum data has been reset.")
            }
            then("check") {
                thenCallback("id", StringArgumentType.word()) {
                    val id = argument<String>("id").let(SkyBlockId::item)
                    val item = SimpleItemAPI.getItemByIdOrNull(id) ?: run {
                        Text.sendDebug("Item not found.")
                        return@thenCallback
                    }
                    Text.sendDebug {
                        append(item.hoverName)
                        append(" Museum status: ")
                        if (isStoredInMuseum(id)) {
                            append("Stored") {
                                color = TextColor.GREEN
                                underlined = true
                            }
                            val item = getItemInMuseum(id)
                            if (item != null) {
                                val list = mutableListOf<Component>()
                                list.add(item.hoverName)
                                list.addAll(item.getLore())
                                hover = Text.multiline(list)
                            }
                        } else if (isDonated(id)) append("Donated") { color = TextColor.AQUA }
                        else append("Not Donated") { color = TextColor.RED }
                    }
                }
                then("all") {
                    thenCallback("donated") {
                        CompletableFuture.runAsync {
                            val string = MuseumData.museumData.allItems.joinToString("\n") {
                                val id = SkyBlockId.item(it)
                                val name = SimpleItemAPI.getItemByIdOrNull(id)?.hoverName?.stripped ?: run {
                                    return@joinToString "- $it: ITEM NOT FOUND"
                                }
                                if (isDonated(id)) "- $name Donated"
                                else "- $name Not Donated"
                            }
                            McClient.clipboard = string
                            Text.sendDebug("Copied donation status of all museum items to clipboard.")
                        }
                    }
                    thenCallback("stored") {
                        CompletableFuture.runAsync {
                            val string = MuseumData.museumData.allItems.joinToString("\n") {
                                val id = SkyBlockId.item(it)
                                val name = SimpleItemAPI.getItemByIdOrNull(id)?.hoverName?.stripped ?: run {
                                    return@joinToString "- $it: ITEM NOT FOUND"
                                }
                                if (isStoredInMuseum(id)) "- $name Stored"
                                else "- $name Not Stored"
                            }
                            McClient.clipboard = string
                            Text.sendDebug("Copied stored status of all museum items to clipboard.")
                        }
                    }
                }
            }
            then("copy") {
                thenCallback("raw") {
                    val items = MuseumStorage.getRawData() ?: return@thenCallback
                    McClient.clipboard = items.toJson(SkyblockAPICodecs.MuseumStorageDataCodec.codec()).toPrettyString()
                    Text.sendDebug("Copied raw Museum data to clipboard.")
                }
                callback {
                    val items = MuseumStorage.getItemsWithCategory()
                    McClient.clipboard = buildString {
                        appendLine("Museum Items:")
                        for ((category, itemList) in items) {
                            appendLine("- $category: ${itemList.size} items")
                            for (item in itemList) {
                                appendLine("  - ${item.cleanName} (${item.getSkyBlockId()?.id})")
                            }
                        }
                    }
                    Text.sendDebug("Copied Museum data to clipboard.")
                }
            }
        }
    }

}
