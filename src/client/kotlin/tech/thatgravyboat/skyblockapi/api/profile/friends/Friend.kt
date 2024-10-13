package tech.thatgravyboat.skyblockapi.api.profile.friends

import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec
import java.util.*

@GenerateCodec
data class Friend(
    val name: String?,
    val uuid: UUID?,
    val bestFriend: Boolean,
    val friendsSince: Instant,
)
