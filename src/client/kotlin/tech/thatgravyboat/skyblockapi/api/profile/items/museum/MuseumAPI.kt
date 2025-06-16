package tech.thatgravyboat.skyblockapi.api.profile.items.museum

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.MuseumStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.SlotClickEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.museum.MuseumData
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Module
object MuseumAPI {

    //region Regex
    private val inventoryGroup = RegexGroup.INVENTORY.group("museum")

    private val inventoryTitleRegex = inventoryGroup.create(
        "title",
        "Museum ➜ (?<category>.+)$",
    )

    private val donateTitleRegex = inventoryGroup.create(
        "donate.title",
        "Confirm Donation",
    )
    //endregion

    fun getAllItems(): List<ItemStack> = MuseumStorage.getAllItems()
    fun getItemsOnCategory(category: MuseumCategory): List<ItemStack> = MuseumStorage.getItemsOnCategory(category)
    fun getItemsWithCategory(): Map<MuseumCategory, List<ItemStack>> = MuseumStorage.getItemsWithCategory()

    private var lastCategory: MuseumCategory? = null
    private var lastArmor: Pair<String, Map<String, ItemStack>>? = null

    private fun ItemStack.isNotDonated(): Boolean = `is`(Items.GRAY_DYE)
    private fun ItemStack.isNotStored(): Boolean = `is`(Items.LIME_DYE)

    private fun getArmorSetInternalName(list: Collection<String>): String? {
        if (list.isEmpty()) return null
        val armorSets = list.mapNotNull { ItemData.getItemData(it)?.museumData?.armorSets?.toSet() }
        if (armorSets.isEmpty()) return null
        val setId = armorSets.reduce { acc, set ->
            acc.intersect(set)
        }.singleOrNull() ?: return null
        MuseumData.getArmorSetFromId(setId) ?: return null
        return setId
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB)
    fun onSlotClick(event: SlotClickEvent) {
        if (event.isInPlayerInventory) return
        if (lastCategory != MuseumCategory.ARMOR_SETS) return
        if (!donateTitleRegex.match(event.title)) return
        val item = event.item
        if (!item.`is`(Items.LIME_TERRACOTTA)) return
        if (item.cleanName != "Confirm Donation") return
        val (armorSetId, armors) = lastArmor ?: return
        MuseumStorage.addArmorSet(armorSetId, armors)
        lastArmor = null
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB)
    fun onInventoryOpen(event: ContainerInitializedEvent) {
        val items = event.containerItems
        if (donateTitleRegex.match(event.title)) { // Store the armor in the current donation inventory
            if (lastCategory != MuseumCategory.ARMOR_SETS) return
            val armors = items.associateByNotNull { it.getSkyBlockId() }
            if (armors.isEmpty()) {
                lastArmor = null
                return
            }
            val armorSetId = getArmorSetInternalName(armors.keys) ?: run {
                lastArmor = null
                return
            }
            lastArmor = armorSetId to armors
            return
        }
        inventoryTitleRegex.findThenNull(event.title, "category") { (categoryName) ->
            val category = MuseumCategory.fromName(categoryName) ?: return@findThenNull
            lastCategory = category
            val filtered = items.filterNot { it.isSkyblockFiller() }
            when(category) {
                MuseumCategory.ARMOR_SETS -> {
                    for (item in filtered) {
                        if (item.isNotDonated()) {
                            val id = MuseumData.getArmorSetIdFromName(item.cleanName) ?: continue
                            MuseumStorage.deleteArmorSet(id)
                        } else if (item.isNotStored()) {
                            val id = MuseumData.getArmorSetIdFromName(item.cleanName) ?: continue
                            MuseumStorage.addNotStoredArmorSet(id)
                        }
                    }
                }
                MuseumCategory.SPECIAL_ITEMS -> {
                    // TODO implement special items
                }
                else -> {
                    filtered.forEach { item ->
                        if (item.isNotDonated()) {
                            val id = RepoItemsAPI.getItemIdByName(item.cleanName) ?: return@forEach
                            MuseumStorage.deleteItem(id)
                        } else if (item.isNotStored()) {
                            val id = RepoItemsAPI.getItemIdByName(item.cleanName) ?: return@forEach
                            MuseumStorage.addNotStoredItem(category, id)
                        } else {
                            val id = item.getSkyBlockId() ?: return@forEach
                            MuseumStorage.addItem(category, id, item)
                        }
                    }
                }
            }
        } ?: return

        val setId = MuseumData.getArmorSetIdFromName(event.title) ?: return
        val rows = event.rowCount ?: return
        for (row in (rows - 1) downTo 0) {
            val index = 3 + (row * 9)
            val item = items[index]
            if (!item.`is`(Items.ARROW)) continue
            val first = item.getRawLore().firstOrNull() ?: continue
            inventoryTitleRegex.findThenNull(first, "category") { (categoryName) ->
                val category = MuseumCategory.fromName(categoryName) ?: return@findThenNull
                if (category != MuseumCategory.ARMOR_SETS) return@findThenNull
                val map = items.associateByNotNull { it.getSkyBlockId() }
                if (map.isEmpty()) return@findThenNull
                MuseumStorage.addArmorSet(setId, map)
            } ?: break
        }
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("sbapi museum") {
            thenCallback("reset") {
                MuseumStorage.reset()
                Text.sendDebug("Museum data has been reset.")
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
                                appendLine("  - ${item.cleanName} (${item.getSkyBlockId()})")
                            }
                        }
                    }
                    Text.sendDebug("Copied Museum data to clipboard.")
                }
            }
        }
    }

}
