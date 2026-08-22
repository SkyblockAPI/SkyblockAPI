package tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata

import com.google.gson.JsonArray
import com.mojang.serialization.Codec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Files

private const val URL = "https://api.hypixel.net/v2/resources/skyblock/items"

@Module
object ItemData {
    //? < 26.2
    //@Deprecated("Use ItemData.data instead", ReplaceWith("data.values")) val itemData: List<HypixelApiItem> get() = data.values.toList()

    private val backupData: Map<String, HypixelApiItem> = SkyBlockAPI.mod.findPath("repo/item_data.json").orElseThrow()
        ?.let(Files::readString)?.readJson<JsonArray>().toDataOrThrow(HypixelApiItem.CODEC.listOf())
        .associateBy(HypixelApiItem::id)

    private var hypixelData: Map<String, HypixelApiItem> = emptyMap()

    init {
        Scheduling.async {
            val response = Http.getResult(URL, SkyblockAPICodecs.getCodec<HypixelItemsResponse>()).getOrNull() ?: return@async
            hypixelData = response.items.associateBy { it.id }
        }
    }

    val data: Map<String, HypixelApiItem> = hypixelData.takeUnless { it.isEmpty() } ?: backupData

    @JvmName("getItemDataFromSkyBlockId")
    fun getItemData(id: SkyBlockId): HypixelApiItem? = getItemData(id.skyblockId)

    fun getItemData(id: String): HypixelApiItem? = data[id.uppercase()]

    //? < 26.2 {
    /*@Deprecated("Use getNpcSellPrice instead", ReplaceWith("getNpcSellPrice(id)"), DeprecationLevel.ERROR)
    fun getNpcPrice(id: String): Int? = getItemData(id)?.npcSellPrice*///?}
    fun getNpcSellPrice(id: String): Float? = getItemData(id)?.npcSellPriceFloat

    fun getMotesSellPrice(id: String): Float? = getItemData(id)?.motesSellPrice
}

@GenerateCodec
data class HypixelItemsResponse(val items: List<HypixelApiItem>)

@GenerateCodec
data class HypixelApiItem(
    val id: String,
    @param:FieldName("gemstone_slots") val gemstones: List<GemstoneCost> = emptyList(),
    @param:FieldName("upgrade_costs") val upgradeCost: List<List<Cost>> = emptyList(),
    @param:FieldName("dungeon_item_conversion_cost") val conversionCost: EssenceCost?,
    @param:FieldName("npc_sell_price") val npcSellPrice: Int?,
    @param:FieldName("npc_sell_price") val npcSellPriceFloat: Float?,
    @param:FieldName("mote_sell_price") val motesSellPrice: Float?,
    @param:FieldName("museum_data") val museumData: ItemMuseumData?,
    @param:FieldName("rift_transferrable") val riftTransferable: Boolean = false,
    val origin: ItemOrigin?,
) {
    companion object {
        val CODEC: Codec<HypixelApiItem> = SkyblockAPICodecs.HypixelApiItemCodec.codec()
    }
}
