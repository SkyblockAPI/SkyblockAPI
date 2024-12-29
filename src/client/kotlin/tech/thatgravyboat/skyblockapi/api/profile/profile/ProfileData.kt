package tech.thatgravyboat.skyblockapi.api.profile.profile

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class ProfileData(
    val profileType: MutableMap<String, ProfileType> = mutableMapOf(),
    val sbLevel: MutableMap<String, Int> = mutableMapOf(),
    val sbLevelProgress: MutableMap<String, Int> = mutableMapOf(),
    val coop: MutableMap<String, Boolean> = mutableMapOf(),
) {
    companion object {
        val CODEC: Codec<ProfileData> = KCodec.getCodec<ProfileData>()
    }
}
