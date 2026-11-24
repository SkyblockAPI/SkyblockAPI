package tech.thatgravyboat.skyblockapi.api.profile.guild

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalNullable
import java.util.UUID

@GenerateCodec
public data class GuildMember(
    val name: String,
    @OptionalNullable
    val uuid: UUID? = null,
)
