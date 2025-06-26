package tech.thatgravyboat.skyblockapi.api.area.slayer

data class SlayerKillProgress(
    override val current: Int,
    override val max: Int,
) : SlayerProgress

data class SlayerXpProgress(
    override val current: Int,
    override val max: Int,
) : SlayerProgress

sealed interface SlayerProgress {
    val current: Int
    val max: Int

    val percentage: Float
        get() = if (max == 0) 0f else (current.toFloat() * 100) / max.toFloat()
}
