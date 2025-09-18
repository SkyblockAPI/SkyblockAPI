package tech.thatgravyboat.skyblockapi.api.remote.hypixel.museum

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.extentions.toScreamingSnakeCase
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Files

@Module
object MuseumData {
    val museumData: RepoMuseumData = SkyBlockAPI.mod.findPath("repo/museum_data.json").orElseThrow()
        ?.let(Files::readString)?.readJson<JsonObject>().toDataOrThrow(RepoMuseumData.CODEC)

    //region Data
    private val exceptions = mapOf(
        "prospector's outfit" to "miner outfit",
        "miner armor" to "tank_miner",
        "salmon armor" to "salmon new",
        "flamebreaker armor" to "flame breaker",
        "ender armor" to "end",
        "maxor's armor" to "speed wither",
        "necron's armor" to "power wither",
        "storm's armor" to "wise wither",
        "goldor's armor" to "tank wither",
        "vanquisher set" to "vanquished",
        "perfect armor - tier xii" to "perfect tier 12",
        "perfect armor - tier xiii" to "perfect tier 13",
    )

    private val armorNames = listOf(
        "set", "suit", "armor", "outfit", "equipment",
        "'s special armor", "'s armor", "armor of", "tuxedo",
    )
    //endregion
    fun isMuseumItem(id: SkyBlockId): Boolean = id.skyblockId in museumData.allItems

    fun getArmorSetIdFromName(name: String): String? {
        val lowercase = name.lowercase().trim()
        val id = exceptions.getOrElse(lowercase) {
            armorNames.map { lowercase.replace(it, "").trim() }.minBy(String::length)
        }.toScreamingSnakeCase()
        if (museumData.armorSets.containsKey(id)) return id
        return lowercase.toScreamingSnakeCase().takeIf { museumData.armorSets.containsKey(it) }
    }

    fun isArmorSet(id: String): Boolean = museumData.armorSets.containsKey(id)

    fun getArmorSetFromId(id: String): List<String>? = museumData.armorSets[id]

    fun getArmorSetFromName(name: String): List<String>? {
        val id = getArmorSetIdFromName(name) ?: return null
        return museumData.armorSets[id]
    }

}


@GenerateCodec
data class RepoMuseumData(
    @FieldName("all_items") val allItems: Set<String> = emptySet(),
    @FieldName("armor_sets") val armorSets: Map<String, List<String>> = emptyMap(),
) {
    companion object {
        val CODEC: Codec<RepoMuseumData> = SkyblockAPICodecs.RepoMuseumDataCodec.codec()
    }
}
