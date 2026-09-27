package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.utils.extentions.enumSetOf

@GenerateCodec
data class MaxwellTuning(
    val stat: SkyBlockStat,
    val value: Double,
) {
    val isEmpty: Boolean get() = value == 0.0
    companion object {
        val ALLOWED_STATS: Set<SkyBlockStat> = enumSetOf(
            SkyBlockStat.HEALTH,
            SkyBlockStat.DEFENSE,
            SkyBlockStat.STRENGTH,
            SkyBlockStat.INTELLIGENCE,
            SkyBlockStat.SPEED,
            SkyBlockStat.CRIT_CHANCE,
            SkyBlockStat.CRIT_DAMAGE,
            SkyBlockStat.ATTACK_SPEED,
        )
    }
}

@GenerateCodec
data class MaxwellTuningTemplate(
    val index: Int,
    var locked: Boolean = true,
    @Compact
    var tunings: Set<MaxwellTuning> = emptySet(),
)
