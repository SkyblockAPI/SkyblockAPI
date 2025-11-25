package tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.generated.EnumCodec

enum class ItemOrigin {
    RIFT,
    BINGO,
    UNKNOWN,
    ;

    companion object {
        @IncludedCodec
        val CODEC: Codec<ItemOrigin> = EnumCodec.forKCodec(entries.toTypedArray()).orElse(UNKNOWN)
    }
}
