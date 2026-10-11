package tech.thatgravyboat.skyblockapi.api.location

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class SkyBlockRegion(
    val areas: Set<SkyBlockArea> = emptySet(),
    val islands: Set<SkyBlockIsland> = emptySet(),
    val biomes: Set<SkyBlockBiome> = emptySet(),
) {
    public fun inArea(): Boolean = SkyBlockArea.inAnyArea(areas)
    public fun inIsland(): Boolean = SkyBlockIsland.inAnyIsland(islands)
    public fun inBiome(): Boolean = SkyBlockBiome.inAnyBiome(biomes)

    public fun inAnyRegion(): Boolean = inArea() || inIsland() || inBiome()

    public companion object {
        public val CODEC: Codec<SkyBlockRegion> = SkyblockAPICodecs.getCodec<SkyBlockRegion>()
    }
}
