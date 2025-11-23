package tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata

import com.google.gson.JsonArray
import com.mojang.serialization.Codec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Files

@Module
object ItemData {
    @Deprecated("Use ItemData.data instead", ReplaceWith("data.values"), DeprecationLevel.ERROR)
    val itemData: List<HypixelApiItem> get() = data.values.toList()

    val data: Map<String, HypixelApiItem> = SkyBlockAPI.mod.findPath("repo/item_data.json").orElseThrow()
        ?.let(Files::readString)?.readJson<JsonArray>().toDataOrThrow(HypixelApiItem.CODEC.listOf())
        .associateBy(HypixelApiItem::id)

    fun getItemData(id: String): HypixelApiItem? = data[id]

    @Deprecated("Use getNpcSellPrice instead", ReplaceWith("getNpcSellPrice(id)"), DeprecationLevel.ERROR)
    fun getNpcPrice(id: String): Int? = getItemData(id)?.npcSellPrice
    fun getNpcSellPrice(id: String): Float? = getItemData(id)?.npcSellPriceFloat
}

@GenerateCodec
data class HypixelApiItem(
    val id: String,
    @param:FieldName("gemstone_slots") val gemstones: List<GemstoneCost> = emptyList(),
    @param:FieldName("upgrade_costs") val upgradeCost: List<List<Cost>> = emptyList(),
    @param:FieldName("dungeon_item_conversion_cost") val conversionCost: EssenceCost?,
    @param:FieldName("npc_sell_price") val npcSellPrice: Int?,
    @param:FieldName("npc_sell_price") val npcSellPriceFloat: Float?,
    @param:FieldName("museum_data") val museumData: ItemMuseumData?,
    @param:FieldName("rift_transferrable") val riftTransferable: Boolean = false,
    @param:FieldName("origin") val itemOrigin: ItemOrigin?,
) {
    companion object {
        val CODEC: Codec<HypixelApiItem> = SkyblockAPICodecs.HypixelApiItemCodec.codec()
    }
}
