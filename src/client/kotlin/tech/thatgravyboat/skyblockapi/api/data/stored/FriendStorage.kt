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
        name: String? = null,
        uuid: UUID? = null,
        bestFriend: Boolean? = null,
        friendsSince: Instant? = null
    ): Boolean {
        if (name == null && uuid == null) return false
        val friend = friends.find {
            (uuid != null && it.uuid == uuid) || (name != null && it.name == name)
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
        val newName = name ?: friend.name
        val newUuid = uuid ?: friend.uuid
        val newBestFriend = bestFriend ?: friend.bestFriend
        friends.add(Friend(newName, newUuid, newBestFriend, newInstant))
        save()
        return true
    }

    fun removeFriend(name: String) {
        val removed = friends.removeIf { it.name == name }
        if (removed) save()
    }

    fun getFriend(name: String): Friend? {
        return friends.find { it.name == name }
    }

    fun getFriend(uuid: UUID): Friend? {
        return friends.find { it.uuid == uuid }
    }

    private fun save() {
        FRIENDS.save()
    }

}
