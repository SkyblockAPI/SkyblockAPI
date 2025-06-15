package tech.thatgravyboat.skyblockapi.api.remote.repo

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.generated.KCodec

@Module
object RepoSlayerData {

    val data: Map<SlayerType, RepoSlayerData> = SkyBlockAPI.getRepo("slayer", Codec.unboundedMap(KCodec.getCodec<SlayerType>(), RepoSlayerData.CODEC))

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
