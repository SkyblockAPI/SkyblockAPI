package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.generated.EnumCodec

enum class Essence(val canBeSold: Boolean = true) {
    WITHER,
    UNDEAD,
    DRAGON,
    SPIDER,
    ICE,
    DIAMOND,
    GOLD,
    CRIMSON,
    SUN_GECKO(false),
    FOREST,
    FOSSIL,
    UNKNOWN(false),
    ;

    val bazaarId: String? = "ESSENCE_${this.name}".takeIf { canBeSold }

    companion object {
        val actualEntries = entries.filterNot { it == UNKNOWN }

        @IncludedCodec
        val CODEC: Codec<Essence> = EnumCodec.forKCodec(entries.toTypedArray()).orElse(UNKNOWN)
    }
}
