package tech.thatgravyboat.skyblockapi.api.location

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.platform.identifier
import kotlin.jvm.optionals.getOrNull

@GenerateCodec
public data class SkyBlockBiome(val biome: Identifier) {
    public fun inBiome() = LocationAPI.biome == this

    public operator fun contains(entity: Entity) = contains(entity.position())
    public operator fun contains(position: Vec3): Boolean = contains(BlockPos(position.x().toInt(), position.y().toInt(), position.z().toInt()))
    public operator fun contains(position: BlockPos): Boolean = McLevel.self?.getBiome(position)?.unwrapKey()?.getOrNull()?.identifier == biome

    companion object {
        public fun inAnyBiome(vararg biomes: SkyBlockBiome) = LocationAPI.biome in biomes
        public fun inAnyBiome(biomes: Collection<SkyBlockBiome>) = LocationAPI.biome in biomes
    }
}

@Suppress("unused")
public object SkyBlockBiomes {

    public const val HYPIXEL_IDENTIFIER = "hypixel"

    internal val registeredBiomes = mutableMapOf<String, SkyBlockBiome>()

    public fun getSkyBlockBiomeOrNull(biome: Identifier): SkyBlockBiome? = registeredBiomes.toList().find { it.second.biome == biome }?.second
    public fun getSkyBlockBiome(biome: Identifier): SkyBlockBiome = getSkyBlockBiomeOrNull(biome) ?: register(biome.path, biome)

    private fun register(key: String, id: String = key) = registeredBiomes.getOrPut(key) { SkyBlockBiome(Identifiers.of(HYPIXEL_IDENTIFIER, key)) }
    private fun register(key: String, id: Identifier) = registeredBiomes.getOrPut(key) { SkyBlockBiome(id) }

    // HUB
    public val WILDERNESS = register("wilderness")
    public val GRAVEYARD = register("graveyard")

    // PARK
    public val BIRCH_FOREST = register("birch_forest")
    public val SPRUCE_FOREST = register("spruce_forest")
    public val DARK_FOREST = register("dark_forest")

    // GALATEA
    public val MOONGLADE = register("moonglade")
    public val TORRHUS = register("torrhus")
    public val MIDNIGHT_FOREST = register("midnight_forest")

    // FISHING
    public val BAYOU = register("bayou")
    public val LOTUS_ATOLL = register("lotus_atoll")

    // SAFARI
    public val CAVERN = register("cavern")
    public val FOREST = register("forest")
    public val HAUNTED = register("haunted")
    public val ICY = register("icy")
    public val ICY_CAVES = register("icy_caves")

    // COMBAT
    public val SPIDERS_DEN = register("spiders_den")

    // Unused biome which seem to be originally planned as like Park
    public val BOG = register("bog")
}
