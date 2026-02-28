package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumCategory
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumItemData
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumStorageData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.museum.MuseumData
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs.getCodec
import tech.thatgravyboat.skyblockapi.utils.extentions.enumMapOf
import tech.thatgravyboat.skyblockapi.utils.extentions.forNullGetter
import tech.thatgravyboat.skyblockapi.utils.extentions.getRarityLineIndex
import tech.thatgravyboat.skyblockapi.utils.extentions.isSameItem
import tech.thatgravyboat.skyblockapi.utils.extentions.withoutGetter
import java.util.Optional
import kotlin.collections.MutableMap
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Module
internal object MuseumStorage {

    //region Codecs
    private val V0_CODEC: Codec<MuseumStorageData> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.INT.optionalFieldOf("milestone", 0).forGetter { it.milestone },
            CodecUtils.map(
                Codec.STRING,
                CodecUtils.map(Codec.STRING, getCodec<ItemStack>().optionalFieldOf("item").codec())
            ).optionalFieldOf("categories").withoutGetter(),
            CodecUtils.map(
                Codec.STRING,
                CodecUtils.map(Codec.STRING, getCodec<ItemStack>()).optionalFieldOf("items").codec()
            ).optionalFieldOf("armorSets").withoutGetter(),
            CodecUtils.mutableList(getCodec<ItemStack>()).optionalFieldOf("specialItems").withoutGetter(),
        ).apply(instance) { milestone, categories, armorSets, specialItems ->
            val newCategories = enumMapOf<MuseumCategory, MutableMap<SkyBlockId, MuseumItemData>>()

            fun addItem(id: String, item: ItemStack?) {
                val sbId = SkyBlockId.item(id)
                val category = ItemData.getItemData(sbId)?.museumData?.category ?: return
                newCategories.getOrPut(category, ::LinkedHashMap)[sbId] = MuseumItemData(item)
            }

            categories.getOrNull()?.forEach { (_, items) ->
                items.forEach { (id, item) ->
                    addItem(id, item.getOrNull())
                }
            }

            armorSets.getOrNull()?.forEach { (armorId, itemsOptional) ->
                val items = itemsOptional.getOrNull()

                if (items.isNullOrEmpty()) {
                    MuseumData.getArmorSetFromId(armorId)?.forEach { id ->
                        addItem(id, null)
                    }
                } else items.forEach(::addItem)
            }

            MuseumStorageData(milestone, newCategories, specialItems.getOrDefault(mutableListOf()))
        }
    }
    //endregion

    private val MUSEUM = StoredProfileData<MuseumStorageData>(
        "museum.json",
        1,
        autoLoadOnProfileSwap = true,
    ) { version ->
        when (version) {
            0 -> V0_CODEC
            1 -> MuseumStorageData.CODEC
            else -> tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils.unit(::MuseumStorageData)
        }
    }

    private val data: MuseumStorageData? get() = MUSEUM.get()

    val milestone: Int get() = data?.milestone ?: 0

    fun setMilestone(level: Int) {
        val data = data ?: return
        if (data.milestone != level) {
            data.milestone = level
            save()
        }
    }

    private fun ItemStack.removeMuseumLines(): ItemStack {
        val index = getRarityLineIndex()
        if (index == -1) return this
        val newItem = this.copy()
        val lore = newItem.get(DataComponents.LORE)?.lines() ?: return this
        val fixedLore = lore.subList(0, index.coerceAtMost(lore.size))
        newItem.set(DataComponents.LORE, ItemLore(fixedLore))
        return newItem
    }

    /** Shouldn't be called for special items */
    fun addItem(category: MuseumCategory, id: SkyBlockId, item: ItemStack) {
        val data = data ?: return
        val cleanedItem = item.removeMuseumLines()
        val items = data.categories.getOrPut(category, ::LinkedHashMap)
        val currentData = items[id]
        if (currentData == null) {
            items[id] = MuseumItemData(cleanedItem)
        } else {
            if (cleanedItem.isSameItem(currentData.item)) return
            currentData.item = cleanedItem
        }
        save()
    }

    /** Shouldn't be called for special items */
    fun addNotStoredItem(category: MuseumCategory, id: SkyBlockId) {
        val data = data ?: return
        val items = data.categories.getOrPut(category, ::LinkedHashMap)
        val currentData = items[id]
        if (currentData == null) {
            items[id] = MuseumItemData(null)
        } else {
            if (currentData.item == null) return // Item is already marked as not stored
            currentData.item = null
        }
        save()
    }

    /**
     * Completely removes said item from the museum storage.
     * Shouldn't be called for special items
     */
    fun deleteItem(id: SkyBlockId) {
        val data = data ?: return
        var shouldSave = false
        for (map in data.categories.values) {
            if (map.remove(id) != null) shouldSave = true
        }
        if (shouldSave) save()
    }

    /** Shouldn't be directly edited and should only be used for debugging purposes. */
    fun getRawData() = data

    fun getAllItems(): List<ItemStack> {
        return MuseumCategory.entries.flatMap { getItemsOnCategory(it) }
    }

    fun getItemsOnCategory(category: MuseumCategory): List<ItemStack> {
        val data = data ?: return emptyList()
        return when (category) {
            MuseumCategory.SPECIAL_ITEMS -> data.specialItems
            else -> data.categories[category]?.values?.mapNotNull(MuseumItemData::item).orEmpty()
        }
    }

    fun getItemsWithCategory(): Map<MuseumCategory, List<ItemStack>> {
        return MuseumCategory.entries.associateWithTo(enumMapOf(), ::getItemsOnCategory)
    }

    fun hasDonatedItem(id: SkyBlockId): Boolean {
        val data = data ?: return false
        return data.categories.values.any { it.containsKey(id) }
    }

    fun isItemStored(id: SkyBlockId): Boolean {
        val data = data ?: return false
        return data.categories.values.any { it[id]?.inMuseum == true }
    }

    fun getItemStored(id: SkyBlockId): ItemStack? {
        val data = data ?: return null
        return data.categories.values.firstNotNullOfOrNull { it[id]?.item }
    }

    fun reset() {
        val data = data ?: return
        data.categories.clear()
        data.specialItems.clear()
        save()
    }

    fun save() = MUSEUM.save()

}
