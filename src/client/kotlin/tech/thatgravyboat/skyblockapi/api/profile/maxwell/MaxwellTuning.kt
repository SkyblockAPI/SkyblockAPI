package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class MaxwellTuning(
    val stat: SkyBlockStat,
    val value: Double,
) {
    companion object {
        val ALLOWED_STATS = setOf(
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
