package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.modules.FieldName
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
@Deprecated("Moved to remote.hypixel.itemdata")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
object ItemData {

    val itemData: List<HypixelApiItem> = emptyList()

    fun getItemData(id: String) = itemData.firstOrNull { it.id == id }
}

@Deprecated("Moved to remote.hypixel.itemdata")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
data class HypixelApiItem(
    val id: String,
    @param:FieldName("gemstone_slots") val gemstones: List<GemstoneCost> = emptyList(),
    @param:FieldName("upgrade_costs") val upgradeCost: List<List<Cost>> = emptyList(),
    @param:FieldName("dungeon_item_conversion_cost") val conversionCost: EssenceCost?,
)
