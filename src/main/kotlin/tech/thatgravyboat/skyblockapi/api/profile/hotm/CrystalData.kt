package tech.thatgravyboat.skyblockapi.api.profile.hotm

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.data.CrystalStatus
import tech.thatgravyboat.skyblockapi.api.data.CrystalType
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class CrystalData(
    var crystals: MutableMap<CrystalType, CrystalStatus> = mutableMapOf(),
) {
    companion object {
        val CODEC: Codec<CrystalData> = SkyblockAPICodecs.getCodec<CrystalData>()
    }
}
