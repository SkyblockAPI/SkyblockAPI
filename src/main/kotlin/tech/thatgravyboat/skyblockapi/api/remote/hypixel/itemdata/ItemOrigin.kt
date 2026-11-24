package tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.generated.EnumCodec

public enum class ItemOrigin {
    RIFT,
    BINGO,
    UNKNOWN,
    ;

    public companion object {
        @IncludedCodec
        public val CODEC: Codec<ItemOrigin> = EnumCodec.forKCodec(entries.toTypedArray()).orElse(UNKNOWN)
    }
}
