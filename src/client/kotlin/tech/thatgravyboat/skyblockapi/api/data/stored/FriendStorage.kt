package tech.thatgravyboat.skyblockapi.api.data.stored

import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.api.data.FriendData
import tech.thatgravyboat.skyblockapi.api.data.StoredPlayerData
import tech.thatgravyboat.skyblockapi.api.profile.friends.Friend
import java.util.*
import kotlin.time.Duration.Companion.days

private val MINIMUM_DIFF = 1.days

internal object FriendStorage {

    private val FRIENDS = StoredPlayerData(
        ::FriendData,
        FriendData.CODEC,
        "friends.json"
    )

    val friends: MutableList<Friend>
        get() = FRIENDS.get().friends

    fun updateFriend(
        name: String,
        uuid: UUID? = null,
        bestFriend: Boolean? = null,
        friendsSince: Instant? = null
    ): Boolean {
        val friend = friends.find {
            (uuid != null && it.uuid == uuid) || it.name == name
        }
        if (friend == null) {
            val instant = friendsSince ?: Instant.DISTANT_PAST
            val newFriend = Friend(name, uuid, bestFriend ?: false, instant)
            friends.add(newFriend)
            save()
            return true
        }
        if (friend.name == name && friend.uuid == uuid || friend.bestFriend == bestFriend) {
            if (friendsSince == null) return false
            val difference = (friend.friendsSince - friendsSince).absoluteValue
            if (difference < MINIMUM_DIFF) return false
        }
        friends.remove(friend)
        val newInstant = friendsSince ?: friend.friendsSince
        val newUuid = uuid ?: friend.uuid
        val newBestFriend = bestFriend ?: friend.bestFriend
        friends.add(Friend(name, newUuid, newBestFriend, newInstant))
        save()
        return true
    }

    fun addFriend(name: String) {
        removeFriend(name)
        friends.add(Friend(name, null, false, Instant.DISTANT_PAST))
    }

    fun removeFriend(name: String) = removeFriends { it.name.equals(name, true) }

    fun removeFriends(predicate: (Friend) -> Boolean) {
        val removed = friends.removeIf(predicate)
        if (removed) save()
    }

    fun getFriend(name: String): Friend? {
        return friends.find { it.name.equals(name, true) }
    }

    fun getFriend(uuid: UUID): Friend? {
        return friends.find { it.uuid == uuid }
    }

    fun clear() {
        friends.clear()
        save()
    }

    private fun save() {
        FRIENDS.save()
    }

}
