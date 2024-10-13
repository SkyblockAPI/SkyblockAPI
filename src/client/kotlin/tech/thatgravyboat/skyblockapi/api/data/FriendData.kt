package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.profile.friends.Friend
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class FriendData(
    val friends: MutableList<Friend> = mutableListOf()
) {
    companion object {
        val CODEC: Codec<FriendData> = KCodec.getCodec<FriendData>()
    }
}
