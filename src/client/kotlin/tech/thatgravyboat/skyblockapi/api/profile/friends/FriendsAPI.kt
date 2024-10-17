package tech.thatgravyboat.skyblockapi.api.profile.friends

import kotlinx.datetime.Clock
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import tech.thatgravyboat.skyblockapi.api.data.stored.FriendStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseDuration
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.CommonRegexes
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.component.find
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import tech.thatgravyboat.skyblockapi.utils.time.ago
import kotlin.time.Duration

@Suppress("unused")
@Module
object FriendsAPI {

    //region Regex
    private val regexGroup = RegexGroup.CHAT.group("friend")
    private val listGroup = regexGroup.group("list")

    private val addedFriendRegex = regexGroup.create(
        "add",
        "^You are now friends with (?:\\[.+] )?(?<name>[a-zA-Z0-9_]+)"
    )
    private val removedFriendRegex = regexGroup.create(
        "remove",
        "^You removed (?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) from your friends list!"
    )

    private val addedBestFriendRegex = regexGroup.create(
        "bestfriend.add",
        "^(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) is now a best friend!"
    )
    private val removeBestFriendRegex = regexGroup.create(
        "bestfriend.remove",
        "^(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) is no longer a best friend!"
    )

    private val pageFriendsListRegex = listGroup.create(
        "page",
        "^\\s*(?:<<)? Friends \\(Page (?<current>\\d+) of (?<max>\\d+)\\)"
    )
    private val friendEntryListRegex = listGroup.create(
        "entry",
        "^(?<name>\\S+) is "
    ).toComponentRegex()
    private val friendsDurationRegex = listGroup.create(
        "duration",
        "Friends for (?<time>.+)\n"
    )
    //endregion

    val friends: List<Friend>
        get() = FriendStorage.friends

    fun isFriend(name: String): Boolean = FriendStorage.getFriend(name) != null

    fun isBestFriend(name: String): Boolean = FriendStorage.getFriend(name)?.bestFriend ?: false

    // Dealing with friends list
    private var currentPage: Int = 0
    private var maxPage: Int = 0
    private var isInFriendsList = false
    private val foundFriends: MutableSet<String> = mutableSetOf()

    @Subscription
    fun onChat(event: ChatReceivedEvent) {
        val components = event.component.splitLines()
        if (components.size == 1) {
            handleSingleLine(components.first())
            return
        }
        if (handleFriendsList(components)) return
        components.any(::handleSingleLine)
    }

    private fun resetListSearch() {
        currentPage = 0
        maxPage = 0
        isInFriendsList = false
        foundFriends.clear()
    }

    private fun handleFriendsList(components: List<Component>): Boolean {
        val secondLine = components[1]
        return pageFriendsListRegex.find(secondLine.stripped, "current", "max") { (current, max) ->
            val currentPos = current.toIntValue()
            val maxPos = max.toIntValue()
            if (maxPage == 0) maxPage = maxPos

            if (maxPos != maxPage || currentPage + 1 != currentPos) {
                resetListSearch()
            } else {
                currentPage = currentPos
                maxPage = maxPos
                isInFriendsList = true
            }

            for (i in 3 until components.lastIndex) {
                val lineComponent = components[i]
                friendEntryListRegex.find(lineComponent, "name") linesFind@{ (component) ->
                    val name = component.stripped
                    val isBestFriend = component.string.contains("§l")
                    val hoverComponent = component.style.hoverEvent?.getValue(HoverEvent.Action.SHOW_TEXT) ?: return@linesFind
                    val timeString = friendsDurationRegex.findGroup(hoverComponent.stripped, "time") ?: return@linesFind
                    val duration: Duration = timeString.parseDuration() ?: Duration.ZERO // TODO: actually parse the duration properly
                    val uuid = CommonRegexes.getUuidFromViewProfile(component)
                    FriendStorage.updateFriend(name, uuid, isBestFriend, duration.ago())
                    foundFriends += name
                }

            }

            if (isInFriendsList && currentPage == maxPage) {
                FriendStorage.removeFriends { it.name !in foundFriends }
                Text.of("updated friends list!").send()
                resetListSearch()
            }
        }
    }

    private fun handleSingleLine(component: Component): Boolean {
        return handleMessage(component).also { if (it) resetListSearch() }
    }

    private fun handleMessage(component: Component): Boolean {
        val message = component.stripped
        addedFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.updateFriend(name = name, bestFriend = false, friendsSince = Clock.System.now())
        } ?: return true
        removedFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.removeFriend(name)
        } ?: return true
        addedBestFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.updateFriend(name = name, bestFriend = true)
        } ?: return true
        removeBestFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.updateFriend(name = name, bestFriend = false)
        } ?: return true
        return false
    }

    @Subscription
    fun onDisconnect(event: ServerDisconnectEvent) = resetListSearch()

}
