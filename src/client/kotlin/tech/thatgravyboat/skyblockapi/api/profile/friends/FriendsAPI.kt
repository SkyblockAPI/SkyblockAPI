package tech.thatgravyboat.skyblockapi.api.profile.friends

import kotlinx.datetime.Clock
import tech.thatgravyboat.skyblockapi.api.data.stored.FriendStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull

@Module
object FriendsAPI {

    //region Regex
    private val regexGroup = RegexGroup.CHAT.group("friend")

    private val addedFriendRegex = regexGroup.create(
        "add",
        "^You are now friends with (?:\\[.+] )?(?<name>[a-zA-Z0-9_]+)"
    )
    private val removedFriendRegex = regexGroup.create(
        "remove",
        "^You removed (?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) from your friends list!"
    )

    // TODO: handle components with multiple lines
    private val addedBestFriendRegex = regexGroup.create(
        "bestfriend.add",
        "\n(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) is now a best friend!"
    )
    private val removeBestFriendRegex = regexGroup.create(
        "bestfriend.remove",
        "\n(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) is no longer a best friend!"
    )
    //endregion

    val friends: List<Friend>
        get() = FriendStorage.friends

    fun isFriend(name: String): Boolean = FriendStorage.getFriend(name) != null

    fun isBestFriend(name: String): Boolean = FriendStorage.getFriend(name)?.bestFriend ?: false

    @Subscription
    fun onChat(event: ChatReceivedEvent) {
        val message = event.text
        addedFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.updateFriend(name = name, bestFriend = false, friendsSince = Clock.System.now())
        } ?: return
        removedFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.removeFriend(name)
        } ?: return
        addedBestFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.updateFriend(name = name, bestFriend = true)
        } ?: return
        removeBestFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.updateFriend(name = name, bestFriend = false)
        } ?: return
    }

}
