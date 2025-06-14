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
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

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
    private var lastArmor: Map<String, ItemStack>? = null

    private fun ItemStack.isNotDonated(): Boolean = `is`(Items.GRAY_DYE)
    private fun ItemStack.isNotStored(): Boolean = `is`(Items.LIME_DYE)

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB)
    fun onSlotClick(event: SlotClickEvent) {
        if (event.isInPlayerInventory) return
        if (lastCategory != MuseumCategory.ARMOR_SETS) return
        if (!donateTitleRegex.match(event.title)) return
        val item = event.item
        if (!item.`is`(Items.LIME_TERRACOTTA)) return
        if (item.cleanName != "Confirm Donation") return
        val lastArmor = lastArmor ?: return
        val armorSet = getArmorSetInternalName(lastArmor.keys) ?: return
        MuseumStorage.addArmorSet(armorSet, lastArmor)
    }

    private fun ItemStack.grabInternalNames(): List<String> {
        return buildList {
            for (line in getRawLore()) {
                if (line.isBlank()) return@buildList
                addIfNotNull(RepoItemsAPI.getItemIdByName(line.trim()))
            }
        }
    }

    private fun getArmorSetInternalName(list: Collection<String>): String? {
        if (list.isEmpty()) return null
        val armorSets = list.mapNotNull { ItemData.getItemData(it)?.museumData?.armorSets?.toSet() }
        if (armorSets.isEmpty()) return null
        val armorSet = armorSets.reduce { acc, set ->
            acc.intersect(set)
        }.singleOrNull() ?: return null
        return armorSet
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB)
    fun onInventoryOpen(event: ContainerInitializedEvent) {
        val items = event.notPlayerInventoryItems
        if (donateTitleRegex.match(event.title)) {
            if (lastCategory != MuseumCategory.ARMOR_SETS) return
            lastArmor = items.filterNot(::isFiller).associateBy { it.getSkyBlockId() }.filterKeysNotNull().ifEmpty { null }
            return
        }
        inventoryTitleRegex.findThenNull(event.title, "category") { (categoryName) ->
            val category = MuseumCategory.fromName(categoryName) ?: return@findThenNull
            lastCategory = category
            when(category) {
                MuseumCategory.ARMOR_SETS -> {
                    for (item in items.filterNot(::isFiller)) {
                        if (item.isNotDonated()) continue // This would require storing the name shown of every single armor set id and idk how to do that
                        if (item.isNotStored()) {
                            val armorSet = getArmorSetInternalName(item.grabInternalNames()) ?: continue
                            MuseumStorage.addNotStoredArmorSet(armorSet)
                        }
                    }
                }
                MuseumCategory.SPECIAL_ITEMS -> {
                    // TODO implement special items
                }
                else -> {
                    items.forEach {
                        if (it.isNotDonated()) {
                            val id = RepoItemsAPI.getItemIdByName(it.cleanName) ?: return@forEach
                            MuseumStorage.deleteItem(id)
                        } else if (it.isNotStored()) {
                            val id = RepoItemsAPI.getItemIdByName(it.cleanName) ?: return@forEach
                            MuseumStorage.addNotStoredItem(category, id)
                        } else {
                            val id = it.getSkyBlockId() ?: return@forEach
                            MuseumStorage.addItem(category, id, it)
                        }
                    }
                }
            }
        } ?: return
        val rows = event.rowCount ?: return
        for (row in (rows - 1) downTo 0) {
            val index = 3 + (row * 9)
            val item = items[index]
            if (!item.`is`(Items.ARROW)) continue
            val first = item.getRawLore().firstOrNull() ?: continue
            inventoryTitleRegex.findThenNull(first, "category") { (categoryName) ->
                val category = MuseumCategory.fromName(categoryName) ?: return@findThenNull
                if (category != MuseumCategory.ARMOR_SETS) return@findThenNull
                val newSublist = items.subList(0, index).filterNot(::isFiller).associateBy { it.getSkyBlockId() }.filterKeysNotNull()
                val armorSet = getArmorSetInternalName(newSublist.keys) ?: return@findThenNull
                MuseumStorage.addArmorSet(armorSet, newSublist)
            } ?: break
        }
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("sbapi museum") {
            thenCallback("reset") {
                MuseumStorage.reset()
                Text.debug("Museum data has been reset.").send()
            }
            callback {
                val items = MuseumStorage.getRawData() ?: return@callback
                McClient.clipboard = items.toJson(SkyblockAPICodecs.MuseumDataCodec.codec()).toPrettyString()
                Text.debug("Copied Museum data to clipboard.").send()
            }
        }
    }

    private fun isFiller(item: ItemStack): Boolean = item.isSkyblockFiller() || item.getSkyBlockId() == null

}
