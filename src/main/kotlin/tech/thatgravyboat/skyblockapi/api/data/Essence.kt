package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.generated.EnumCodec

public enum class Essence(public val canBeSold: Boolean = true) {
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

    public val bazaarId: String? = "ESSENCE_${this.name}".takeIf { canBeSold }

    public companion object {
        public val actualEntries: List<Essence> = entries.filterNot { it == UNKNOWN }

        @IncludedCodec
        public val CODEC: Codec<Essence> = EnumCodec.forKCodec(entries.toTypedArray()).orElse(UNKNOWN)
    }
}
