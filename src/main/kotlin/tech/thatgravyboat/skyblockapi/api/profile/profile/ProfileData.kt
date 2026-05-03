package tech.thatgravyboat.skyblockapi.api.profile.profile

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import java.util.UUID

@GenerateCodec
data class ProfileData(
    val profileType: MutableMap<String, ProfileType> = mutableMapOf(),
    val sbLevel: MutableMap<String, Int> = mutableMapOf(),
    val sbLevelProgress: MutableMap<String, Int> = mutableMapOf(),
    val coop: MutableMap<String, Boolean> = mutableMapOf(),
    var profileId: MutableMap<String, UUID> = mutableMapOf(),
    var bingoRank: SkyBlockRarity?,
) {
    companion object {
        val CODEC: Codec<ProfileData> = SkyblockAPICodecs.getCodec<ProfileData>()
    }
}
