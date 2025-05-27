package tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata

import com.google.gson.JsonArray
import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec
import tech.thatgravyboat.skyblockapi.modules.FieldName
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Files

@Module
object ItemData {

    val itemData: List<HypixelApiItem> = SkyBlockAPI.mod.findPath("repo/item_data.json").orElseThrow()
        ?.let(Files::readString)?.readJson<JsonArray>().toDataOrThrow(HypixelApiItem.CODEC.listOf())

    fun getItemData(id: String) = itemData.firstOrNull { it.id == id }
}

@GenerateCodec
data class HypixelApiItem(
    val id: String,
    @param:FieldName("gemstone_slots") val gemstones: List<GemstoneCost> = emptyList(),
    @param:FieldName("upgrade_costs") val upgradeCost: List<List<Cost>> = emptyList(),
    @param:FieldName("dungeon_item_conversion_cost") val conversionCost: EssenceCost?,
) {
    companion object {
        internal val CODEC: Codec<HypixelApiItem> = KCodec.getCodec<HypixelApiItem>()
    }
}
