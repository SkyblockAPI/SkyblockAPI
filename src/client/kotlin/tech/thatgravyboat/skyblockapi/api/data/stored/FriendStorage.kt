package tech.thatgravyboat.skyblockapi.api.data.stored

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
    ): Boolean {
        val friend = friends.find {
            (uuid != null && it.uuid == uuid) || it.name == name
        }
        if (friend == null) {
            val newFriend = Friend(name, uuid, bestFriend ?: false)
            friends.add(newFriend)
            save()
            return true
        }
        if (friend.name == name && friend.uuid == uuid && friend.bestFriend == bestFriend) return false
        friends.remove(friend)
        val newUuid = uuid ?: friend.uuid
        val newBestFriend = bestFriend ?: friend.bestFriend
        friends.add(Friend(name, newUuid, newBestFriend))
        save()
        return true
    }

    fun addFriend(name: String): Friend {
        val friend = Friend(name, null, false)
        friends.add(friend)
        save()
        return friend
    }

    fun addIfAbsent(name: String): Friend {
        val friend = friends.find { it.name.equals(name, true) }
        return friend ?: addFriend(name)
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
