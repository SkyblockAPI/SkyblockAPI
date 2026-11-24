package tech.thatgravyboat.skyblockapi.api.events.hypixel

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.profile.friends.Friend

public sealed class FriendEvent(public val friend: Friend) : SkyBlockEvent() {
    public class Join(friend: Friend) : FriendEvent(friend)
    public class Leave(friend: Friend) : FriendEvent(friend)
}
