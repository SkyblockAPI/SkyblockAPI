package tech.thatgravyboat.skyblockapi.api.events.hypixel

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.profile.friends.Friend

open class FriendEvent(val friend: Friend) : SkyBlockEvent() {
    class Join(friend: Friend) : FriendEvent(friend)
    class Leave(friend: Friend) : FriendEvent(friend)
}
