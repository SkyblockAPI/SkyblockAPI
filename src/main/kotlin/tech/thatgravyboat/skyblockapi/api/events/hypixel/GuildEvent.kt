package tech.thatgravyboat.skyblockapi.api.events.hypixel

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.profile.guild.GuildMember


public sealed class GuildEvent(public val guildMember: GuildMember) : SkyBlockEvent() {
    public class Join(guildMember: GuildMember) : GuildEvent(guildMember)
    public class Leave(guildMember: GuildMember) : GuildEvent(guildMember)
}
