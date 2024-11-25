package tech.thatgravyboat.skyblockapi.api.profile.friends

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import tech.thatgravyboat.skyblockapi.api.data.stored.FriendStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.FriendEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.modules.Module
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
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import java.util.*

@Suppress("unused")
@Module
object FriendsAPI {

    //region Regex
    private val regexGroup = RegexGroup.CHAT.group("friend")
    private val listGroup = regexGroup.group("list")

    private val addedFriendRegex = regexGroup.create(
        "add",
        "^You are now friends with (?:\\[.+] )?(?<name>[a-zA-Z0-9_]+)",
    )
    private val removedFriendRegex = regexGroup.create(
        "remove",
        "^You removed (?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) from your friends list!",
    )

    private val addedBestFriendRegex = regexGroup.create(
        "bestfriend.add",
        "^(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) is now a best friend!",
    )
    private val removeBestFriendRegex = regexGroup.create(
        "bestfriend.remove",
        "^(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) is no longer a best friend!",
    )

    private val pageFriendsListRegex = listGroup.create(
        "page",
        "^\\s*(?:<<)? Friends \\(Page (?<current>\\d+) of (?<max>\\d+)\\)",
    )
    private val friendEntryListRegex = listGroup.create(
        "entry",
        "^(?<name>\\S+?)(?<nick>\\*)? is ",
    ).toComponentRegex()
    private val friendEntryHoverNameRegex = listGroup.create(
        "entry.hover",
        "Click here to view (?<name>[a-zA-Z0-9_]+)'s profile"
    )

    private val friendJoinLeaveRegex = regexGroup.create(
        "joinleave",
        "^Friend > (?<name>\\S+) (?<action>joined|left)",
    )
    //endregion

    val friends: List<Friend>
        get() = FriendStorage.friends

    fun isFriend(name: String): Boolean = FriendStorage.getFriend(name) != null

    fun isFriend(uuid: UUID): Boolean = FriendStorage.getFriend(uuid) != null

    fun isBestFriend(name: String): Boolean = FriendStorage.getFriend(name)?.bestFriend ?: false

    fun isBestFriend(uuid: UUID): Boolean = FriendStorage.getFriend(uuid)?.bestFriend ?: false

    fun getFriend(name: String): Friend? = FriendStorage.getFriend(name)

    // Dealing with friends list
    private var currentPage: Int = 0
    private var maxPage: Int = 0
    private var isInFriendsList = false
    private val foundFriends: MutableSet<String> = mutableSetOf()

    @Subscription(priority = Int.MIN_VALUE)
    fun onChat(event: ChatReceivedEvent) {
        val components = event.component.splitLines()
        if (components.size == 1) {
            handleSingleLine(components.first())
            return
        }
        if (handleFriendsList(components)) return
        components.any(::handleSingleLine)
    }

    // TODO: also call this on guild join/leave message if the person is in your friends list
    internal fun resetListSearch() {
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

            for (i in 2 until components.lastIndex) {
                val lineComponent = components[i]
                friendEntryListRegex.find(lineComponent) friendsList@{
                    val component = it["name"] ?: return@friendsList
                    var nick: String? = null
                    val name: String
                    val isNick = it["nick"] != null
                    if (isNick) {
                        nick = component.stripped
                        val value = component.style.hoverEvent?.getValue(HoverEvent.Action.SHOW_TEXT) ?: return@friendsList
                        name = friendEntryHoverNameRegex.findGroup(value.stripped, "name") ?: return@friendsList
                    } else {
                        name = component.stripped
                    }
                    val isBestFriend = component.string.contains("§l")
                    val uuid = CommonRegexes.getUuidFromViewProfile(component)
                    FriendStorage.updateFriend(name, uuid, isBestFriend, nick)
                    foundFriends += name
                }
            }

            if (isInFriendsList && currentPage == maxPage) {
                FriendStorage.removeFriends { it.name !in foundFriends }
                resetListSearch()
            }
        }
    }

    private fun handleSingleLine(component: Component): Boolean {
        return handleMessage(component).also { if (it) resetListSearch() }
    }

    private fun handleMessage(component: Component): Boolean {
        val message = component.stripped
        friendJoinLeaveRegex.findThenNull(message, "name", "action") { (name, action) ->
            val friend = FriendStorage.addIfAbsent(name)
            val joined = action == "joined"

            if (joined) FriendEvent.Join(friend).post()
            else FriendEvent.Leave(friend).post()
        } ?: return true
        addedFriendRegex.findThenNull(message, "name") { (name) ->
            FriendStorage.updateFriend(name = name, bestFriend = false)
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
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        val provider = SuggestionProvider<FabricClientCommandSource> { _, builder ->
            val friends = friends.map { it.name }
            SharedSuggestionProvider.suggest(friends, builder)
        }
        event.register("sbapi") {
            then("friends") {
                then("add") {
                    then("name", StringArgumentType.string()) {
                        callback {
                            val name = StringArgumentType.getString(this, "name") ?: return@callback
                            FriendStorage.removeFriend(name)
                            FriendStorage.addFriend(name)
                            Text.debug("Added $name to friends list.").send()
                        }
                    }
                }
                then("remove") {
                    then("name", StringArgumentType.string(), provider) {
                        callback {
                            val name = StringArgumentType.getString(this, "name") ?: return@callback
                            FriendStorage.removeFriend(name)
                            Text.debug("Removed $name from friends list.").send()
                        }
                    }
                }
                then("list") {
                    callback {
                        val friends = friends
                        if (friends.isEmpty()) {
                            Text.debug("You have no friends. :(").send()
                            return@callback
                        }
                        Text.debug("Friends (${friends.size}):").send()
                        friends.forEach { friend ->
                            Text.debug(" - ${friend.name}") {
                                if (friend.bestFriend) {
                                    val friendText = Text.of(" (Best Friend)") {
                                        this.color = TextColor.GREEN
                                        this.bold = true
                                    }
                                    append(friendText)
                                }
                                if (friend.nickname != null) {
                                    val friendText = Text.of(" (Nick: ${friend.nickname})") {
                                        this.color = TextColor.YELLOW
                                    }
                                    append(friendText)
                                }
                            }.send()
                        }
                    }
                }
                then("check") {
                    then("name", StringArgumentType.string()) {
                        callback {
                            val name = StringArgumentType.getString(this, "name") ?: return@callback
                            val friend = FriendStorage.getFriend(name)
                            if (friend == null) {
                                Text.debug("$name is not your friend.").send()
                                return@callback
                            }
                            val bestFriend = if (friend.bestFriend) "Best Friend" else "Friend"
                            val friendText = Text.of(bestFriend) {
                                this.color = TextColor.GREEN
                                this.bold = friend.bestFriend
                            }
                            Text.debug("${friend.name} is your ") {
                                append(friendText)
                                if (friend.nickname != null) {
                                    val nicknameText = Text.of(" (Nick: ${friend.nickname})") {
                                        this.color = TextColor.YELLOW
                                    }
                                    append(nicknameText)
                                }
                            }.send()
                        }
                    }
                }
                then("clear") {
                    callback {
                        FriendStorage.clear()
                        Text.debug("Cleared friends list.").send()
                    }
                }
            }
        }
    }

    @Subscription
    fun onDisconnect(event: ServerDisconnectEvent) = resetListSearch()

}
