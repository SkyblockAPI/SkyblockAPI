package tech.thatgravyboat.skyblockapi.api.profile.items.sacks

import kotlinx.datetime.Instant
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.time.currentInstant

@GenerateCodec(createCodecMethod = true)
internal data class SacksData(
    var items: MutableList<SackEntry> = mutableListOf(),
) {
    companion object {
        internal val CODEC = SkyblockAPICodecs.SacksDataCodec.codec()
    }
}

@GenerateCodec
internal data class SackEntry(
    val id: String,
    val amount: Int,
    val lastUpdated: Instant = currentInstant(),
)
