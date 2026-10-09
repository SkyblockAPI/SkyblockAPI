package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.profile.friends.Friend
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class FriendData(
    val friends: MutableList<Friend> = mutableListOf()
) {
    public companion object {
        public val CODEC: Codec<FriendData> = SkyblockAPICodecs.getCodec<FriendData>()
    }
}
