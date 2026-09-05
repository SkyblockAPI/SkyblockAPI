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
data class SkyBlockBiome(val biome: Identifier) {
    fun inBiome() = LocationAPI.biome == this

    operator fun contains(entity: Entity) = contains(entity.position())
    operator fun contains(position: Vec3): Boolean = contains(BlockPos(position.x().toInt(), position.y().toInt(), position.z().toInt()))
    operator fun contains(position: BlockPos): Boolean = McLevel.self?.getBiome(position)?.unwrapKey()?.getOrNull()?.identifier == biome

    companion object {
        fun inAnyBiome(vararg biomes: SkyBlockBiome) = LocationAPI.biome in biomes
        fun inAnyBiome(biomes: Collection<SkyBlockBiome>) = LocationAPI.biome in biomes
    }
}

@Suppress("unused")
object SkyBlockBiomes {

    const val HYPIXEL_IDENTIFIER = "hypixel"

    internal val registeredBiomes = mutableMapOf<String, SkyBlockBiome>()

    fun getSkyBlockBiomeOrNull(biome: Identifier): SkyBlockBiome? = registeredBiomes.toList().find { it.second.biome == biome }?.second
    fun getSkyBlockBiome(biome: Identifier): SkyBlockBiome = getSkyBlockBiomeOrNull(biome) ?: register(biome.path, biome)

    private fun register(key: String, id: String = key) = registeredBiomes.getOrPut(key) { SkyBlockBiome(Identifiers.of(HYPIXEL_IDENTIFIER, key)) }
    private fun register(key: String, id: Identifier) = registeredBiomes.getOrPut(key) { SkyBlockBiome(id) }

    // HUB
    val WILDERNESS = register("wilderness")
    val GRAVEYARD = register("graveyard")

    // PARK
    val BIRCH_FOREST = register("birch_forest")
    val SPRUCE_FOREST = register("spruce_forest")
    val DARK_FOREST = register("dark_forest")

    // GALATEA
    val MOONGLADE = register("moonglade")
    val TORRHUS = register("torrhus")
    val MIDNIGHT_FOREST = register("midnight_forest")

    // FISHING
    val BAYOU = register("bayou")
    val LOTUS_ATOLL = register("lotus_atoll")

    // SAFARI
    val CAVERN = register("cavern")
    val FOREST = register("forest")
    val HAUNTED = register("haunted")
    val ICY = register("icy")
    val ICY_CAVES = register("icy_caves")

    // COMBAT
    val SPIDERS_DEN = register("spiders_den")

    // Unused biome which seem to be originally planned as like Park
    val BOG = register("bog")
}
