package tech.thatgravyboat.skyblockapi.api.location

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.platform.identifier
import tech.thatgravyboat.skyblockapi.utils.RegisterableValues
import tech.thatgravyboat.skyblockapi.utils.devutils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.pluralize
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.jvm.optionals.getOrNull

@GenerateCodec
public data class SkyBlockBiome(val biome: Identifier) {
    public fun inBiome(): Boolean = LocationAPI.biome == this

    public operator fun contains(entity: Entity): Boolean = contains(entity.position())
    public operator fun contains(position: Vec3): Boolean = contains(BlockPos(position.x().toInt(), position.y().toInt(), position.z().toInt()))
    public operator fun contains(position: BlockPos): Boolean = McLevel.self?.getBiome(position)?.unwrapKey()?.getOrNull()?.identifier == biome

    public companion object {
        public fun inAnyBiome(vararg biomes: SkyBlockBiome): Boolean = LocationAPI.biome in biomes
        public fun inAnyBiome(biomes: Collection<SkyBlockBiome>): Boolean = LocationAPI.biome in biomes
    }
}

@Suppress("unused")
@Module
public object SkyBlockBiomes : RegisterableValues<SkyBlockBiome>() {

    override val name: String = "biome"
    override val debugToggle: DebugToggle = debugToggle("unknown.biome", "Warns when detecting a new unknown skyblock biome")
    public const val HYPIXEL_IDENTIFIER: String = "hypixel"

    public fun getSkyBlockBiomeOrNull(biome: Identifier): SkyBlockBiome? = registered[biome.path]
    public fun getSkyBlockBiome(biome: Identifier): SkyBlockBiome = getSkyBlockBiomeOrNull(biome) ?: register(biome.path, biome)

    private fun register(key: String, id: String = key) = getOrCreate(key) { SkyBlockBiome(Identifiers.of(HYPIXEL_IDENTIFIER, key)) }
    private fun register(key: String, id: Identifier) = getOrCreate(key) { SkyBlockBiome(id) }

    // HUB
    public val WILDERNESS: SkyBlockBiome = register("wilderness")
    public val GRAVEYARD: SkyBlockBiome = register("graveyard")

    // PARK
    public val BIRCH_FOREST: SkyBlockBiome = register("birch_forest")
    public val SPRUCE_FOREST: SkyBlockBiome = register("spruce_forest")
    public val DARK_FOREST: SkyBlockBiome = register("dark_forest")

    // GALATEA
    public val MOONGLADE: SkyBlockBiome = register("moonglade")
    public val TORRHUS: SkyBlockBiome = register("torrhus")
    public val MIDNIGHT_FOREST: SkyBlockBiome = register("midnight_forest")

    // FISHING
    public val BAYOU: SkyBlockBiome = register("bayou")
    public val LOTUS_ATOLL: SkyBlockBiome = register("lotus_atoll")

    // SAFARI
    public val CAVERN: SkyBlockBiome = register("cavern")
    public val FOREST: SkyBlockBiome = register("forest")
    public val HAUNTED: SkyBlockBiome = register("haunted")
    public val ICY: SkyBlockBiome = register("icy")
    public val ICY_CAVES: SkyBlockBiome = register("icy_caves")

    // COMBAT
    public val SPIDERS_DEN: SkyBlockBiome = register("spiders_den")

    // Unused biome which seem to be originally planned as like Park
    public val BOG: SkyBlockBiome = register("bog")

    override fun debugCommand() {
        McClient.clipboard = missing.joinToString(prefix = "[", postfix = "]")
        val size = missing.size
        Text.sendDebug("Copied $size unknown ${pluralize(size, "biome")} to clipboard.")
    }
}
