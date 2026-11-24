package tech.thatgravyboat.skyblockapi.api.area.slayer

public data class SlayerKillProgress(
    override val current: Int,
    override val max: Int,
) : SlayerProgress

public data class SlayerXpProgress(
    override val current: Int,
    override val max: Int,
) : SlayerProgress

public sealed interface SlayerProgress {
    public val current: Int
    public val max: Int

    public val percentage: Float
        get() = if (max == 0) 0f else (current.toFloat() * 100) / max.toFloat()
}
