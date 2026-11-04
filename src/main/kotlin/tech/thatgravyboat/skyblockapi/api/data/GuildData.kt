package tech.thatgravyboat.skyblockapi.api.data

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalIfEmpty
import me.owdding.ktcodecs.OptionalNullable
import tech.thatgravyboat.skyblockapi.api.profile.guild.GuildMember

@GenerateCodec
internal data class GuildData(
    @OptionalNullable
    var name: String? = null,
    @OptionalIfEmpty
    val members: MutableSet<GuildMember> = mutableSetOf(),
)
