package tech.thatgravyboat.skyblockapi.api.data.stored

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumArmorData
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumCategory
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumItemData
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumStorageData
import tech.thatgravyboat.skyblockapi.utils.extentions.enumMapOf
import tech.thatgravyboat.skyblockapi.utils.extentions.getRarityLineIndex
import tech.thatgravyboat.skyblockapi.utils.extentions.isSameItem

internal object MuseumStorage {

    private val MUSEUM = StoredProfileData<MuseumStorageData>("museum.json")

    private val data: MuseumStorageData? get() = MUSEUM.get()

    private fun ItemStack.removeMuseumLines(): ItemStack {
        val index = getRarityLineIndex()
        if (index == -1) return this
        val newItem = this.copy()
        val lore = newItem.get(DataComponents.LORE)?.lines ?: return this
        val fixedLore = lore.subList(0, index.coerceAtMost(lore.size))
        newItem.set(DataComponents.LORE, ItemLore(fixedLore))
        return newItem
    }

    /** Should only be called for Weapons and Rarities */
    fun addItem(category: MuseumCategory, internalName: String, item: ItemStack) {
        val data = data ?: return
        val cleanedItem = item.removeMuseumLines()
        val items = data.categories.getOrPut(category, ::LinkedHashMap)
        val currentData = items[internalName]
        if (currentData == null) {
            items[internalName] = MuseumItemData(cleanedItem)
        } else {
            if (cleanedItem.isSameItem(currentData.item)) return
            currentData.item = cleanedItem
        }
        save()
    }

    fun addNotStoredItem(category: MuseumCategory, internalName: String) {
        val data = data ?: return
        val items = data.categories.getOrPut(category, ::LinkedHashMap)
        val currentData = items[internalName]
        if (currentData == null) {
            items[internalName] = MuseumItemData(null)
        } else {
            if (currentData.item == null) return // Item is already marked as not stored
            currentData.item = null
        }
        save()
    }

    /** Completely removes said item from the museum storage. */
    fun deleteItem(internalName: String) {
        val data = data ?: return
        var shouldSave = false
        for (map in data.categories.values) {
            if (map.remove(internalName) != null) shouldSave = true
        }
        if (shouldSave) save()
    }

    fun addArmorSet(armorSetName: String, items: Map<String, ItemStack>) {
        val data = data ?: return
        val armorSet = data.armorSets.getOrPut(armorSetName, ::MuseumArmorData)
        var shouldSave = false
        for ((internalName, item) in items) {
            val prev = armorSet.items[internalName]
            val cleanedItem = item.removeMuseumLines()
            if (!cleanedItem.isSameItem(prev)) {
                armorSet.items[internalName] = cleanedItem
                shouldSave = true
            }
        }
        if (shouldSave) save()
    }

    fun addNotStoredArmorSet(armorSetName: String) {
        val data = data ?: return
        var shouldSave = false
        val armorSet = data.armorSets.getOrPut(armorSetName) {
            shouldSave = true
            MuseumArmorData()
        }
        if (armorSet.inMuseum) {
            armorSet.items.clear()
            shouldSave = true
        }
        if (shouldSave) save()
    }

    fun deleteArmorSet(armorSetName: String) {
        val data = data ?: return
        if (data.armorSets.remove(armorSetName) != null) save()
    }

    /** Shouldn't be directly edited and should only be used for debugging purposes. */
    fun getRawData() = data

    fun getAllItems(): List<ItemStack> {
        return MuseumCategory.entries.flatMap { getItemsOnCategory(it)}
    }

    fun getItemsOnCategory(category: MuseumCategory): List<ItemStack> {
        val data = data ?: return emptyList()
        return when(category) {
            MuseumCategory.ARMOR_SETS -> data.armorSets.values.flatMap { it.items.values }
            MuseumCategory.SPECIAL_ITEMS -> data.specialItems
            else -> data.categories[category]?.values?.mapNotNull(MuseumItemData::item) ?: emptyList()
        }
    }

    fun getItemsWithCategory(): Map<MuseumCategory, List<ItemStack>> {
        return MuseumCategory.entries.associateWithTo(enumMapOf(), ::getItemsOnCategory)
    }

    fun reset() {
        val data = data ?: return
        data.categories.clear()
        data.armorSets.clear()
        data.specialItems.clear()
        save()
    }

    private fun save() = MUSEUM.save()

}
