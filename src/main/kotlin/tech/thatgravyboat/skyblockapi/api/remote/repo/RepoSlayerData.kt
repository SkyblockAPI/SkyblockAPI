package tech.thatgravyboat.skyblockapi.api.remote.repo

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@Module
public object RepoSlayerData {

    public val data: Map<SlayerType, RepoSlayerData> = SkyBlockAPI.getRepo("slayer", Codec.unboundedMap(SkyblockAPICodecs.getCodec<SlayerType>(), RepoSlayerData.CODEC))

    public fun getData(type: SlayerType): RepoSlayerData = data[type] ?: error("No slayer data found for $type")

    @GenerateCodec
    public data class RepoSlayerData(
        val name: String,
        val id: String,
        val leveling: List<Long>,
        @FieldName("boss_xp") val bossXp: List<Int>,
    ) {
        val maxBossTier: Int = bossXp.size
        val maxLevel: Int = leveling.size

        public fun getLevel(xp: Long): Int = leveling.indexOfLast { it <= xp } + 1

        public companion object {
            public val CODEC: Codec<RepoSlayerData> = SkyblockAPICodecs.getCodec<RepoSlayerData>()
        }
    }
}
