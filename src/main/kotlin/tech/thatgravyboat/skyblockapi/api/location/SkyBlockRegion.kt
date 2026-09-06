package tech.thatgravyboat.skyblockapi.api.location

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class SkyBlockRegion(
    val areas: Set<SkyBlockArea> = emptySet(),
    val islands: Set<SkyBlockIsland> = emptySet(),
    val biomes: Set<SkyBlockBiome> = emptySet(),
) {
    fun inArea() = SkyBlockArea.inAnyArea(areas)
    fun inIsland() = SkyBlockIsland.inAnyIsland(islands)
    fun inBiome() = SkyBlockBiome.inAnyBiome(biomes)

    fun inAnyRegion() = inArea() || inIsland() || inBiome()

    companion object {
        val CODEC: Codec<SkyBlockRegion> = SkyblockAPICodecs.getCodec<SkyBlockRegion>()
    }
}
