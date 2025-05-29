package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.KCodec

@GenerateCodec
data class HotmData(
    var perks: MutableMap<String, HotmPerk> = mutableMapOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<HotmData>()
    }
}

@GenerateCodec
data class HotmPerk(
    val level: Int,
    val unlocked: Boolean,
    val disabled: Boolean,
)
