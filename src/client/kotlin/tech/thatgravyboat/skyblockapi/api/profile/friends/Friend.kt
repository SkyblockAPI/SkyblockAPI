package tech.thatgravyboat.skyblockapi.api.profile.friends

import me.owdding.ktcodecs.GenerateCodec
import java.util.*

@GenerateCodec
data class Friend(
    val name: String,
    val uuid: UUID?,
    val bestFriend: Boolean,
    val nickname: String?,
)
