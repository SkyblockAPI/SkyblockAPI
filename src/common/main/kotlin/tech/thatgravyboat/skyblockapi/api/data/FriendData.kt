package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.profile.friends.Friend
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class FriendData(
    val friends: MutableList<Friend> = mutableListOf()
) {
    companion object {
        val CODEC: Codec<FriendData> = SkyblockAPICodecs.getCodec<FriendData>()
    }
}
