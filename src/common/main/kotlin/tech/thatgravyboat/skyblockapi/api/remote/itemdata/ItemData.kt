package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import me.owdding.ktcodecs.FieldName
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData as NewItemData

@RemoveNextVersion(
    ReplaceWith(
        "ItemData",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData",
    ),
)
object ItemData {
    val itemData: List<HypixelApiItem> by lazy {
        NewItemData.itemData.map {
            HypixelApiItem(
                id = it.id,
                gemstones = it.gemstones.map {
                    GemstoneCost(
                        slotType = it.slotType,
                        cost = it.cost.mapNotNull { Cost.fromNew(it) },
                    )
                },
                conversionCost = Cost.fromNew(it.conversionCost) as? EssenceCost,
                upgradeCost = it.upgradeCost.map {
                    it.mapNotNull { Cost.fromNew(it) }
                },
            )
        }
    }

    fun getItemData(id: String) = itemData.firstOrNull { it.id == id }
}

@RemoveNextVersion(
    ReplaceWith(
        "HypixelApiItem",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.HypixelApiItem",
    ),
)
data class HypixelApiItem(
    val id: String,
    @param:FieldName("gemstone_slots") val gemstones: List<GemstoneCost> = emptyList(),
    @param:FieldName("upgrade_costs") val upgradeCost: List<List<Cost>> = emptyList(),
    @param:FieldName("dungeon_item_conversion_cost") val conversionCost: EssenceCost?,
)
