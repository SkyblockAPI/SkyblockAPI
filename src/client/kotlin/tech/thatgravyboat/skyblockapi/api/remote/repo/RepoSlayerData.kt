package tech.thatgravyboat.skyblockapi.api.remote.repo

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec
import tech.thatgravyboat.skyblockapi.modules.FieldName
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Files

@Module
object RepoSlayerData {

    private val CODEC: Codec<MutableMap<SlayerType, RepoSlayerData>> =
        CodecUtils.map(KCodec.getCodec<SlayerType>(), RepoSlayerData.CODEC)

    val data: Map<SlayerType, RepoSlayerData> = SkyBlockAPI.mod.findPath("repo/slayer.json").orElseThrow()
        ?.let(Files::readString)?.readJson<JsonObject>().toDataOrThrow(CODEC)

    fun getData(type: SlayerType) = data[type] ?: error("No slayer data found for $type")

    @GenerateCodec
    data class RepoSlayerData(
        val name: String,
        val id: String,
        val leveling: List<Long>,
        @FieldName("boss_xp") val bossXp: List<Int>,
    ) {
        val maxBossTier = bossXp.size
        val maxLevel = leveling.size

        fun getLevel(xp: Long) = leveling.indexOfLast { it <= xp } + 1

        companion object {
            val CODEC: Codec<RepoSlayerData> = KCodec.getCodec<RepoSlayerData>()
        }
    }
}
