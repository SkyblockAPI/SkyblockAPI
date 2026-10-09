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
    public fun inArea() = SkyBlockArea.inAnyArea(areas)
    public fun inIsland() = SkyBlockIsland.inAnyIsland(islands)
    public fun inBiome() = SkyBlockBiome.inAnyBiome(biomes)

    public fun inAnyRegion() = inArea() || inIsland() || inBiome()

    companion object {
        public val CODEC: Codec<SkyBlockRegion> = SkyblockAPICodecs.getCodec<SkyBlockRegion>()
    }
}
