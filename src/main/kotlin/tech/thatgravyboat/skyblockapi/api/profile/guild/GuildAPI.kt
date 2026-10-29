package tech.thatgravyboat.skyblockapi.api.profile.guild

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.GuildStorage
import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerCacheStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.GuildEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.PlayerCacheUpdate
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.devutils.SkyBlockApiDevUtils.debugString
import tech.thatgravyboat.skyblockapi.utils.devutils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.regex.CommonRegexes
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findAll
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.findOrNull
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.findWhen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Module
public object GuildAPI {

    //region Regexes
    private val regexGroup = RegexGroup.CHAT.group("guild")

    private val guildNameRegex = regexGroup.create(
        "name",
        "Guild Name: (?<name>.+)"
    )

    private val guildMessageRegex = regexGroup.create(
        "message",
        """^(?<type>Guild|Officer) > (?:\[[^]]+] )?(?<name>[a-zA-Z0-9_]+)(?: \[[^]]+])?: (?<message>.+)"""
    ).toComponentRegex()

    private val guildOnlineJoinLeaveRegex = regexGroup.create(
        "joinleave_online",
        """^Guild > (?<name>\S+) (?<action>joined|left)"""
    )

    // we don't have an "own joined guild" regex because the message "You joined _____!" is way too ambiguous
    private val ownLeftGuild = regexGroup.create(
        "own.left",
        "^You left the guild"
    )

    private val otherGuildLeaveRegex = regexGroup.create(
        "other.left",
        "^(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) (?:left|was kicked from) the guild"
    )
    private val otherGuildJoinRegex = regexGroup.create(
        "other.join",
        "^(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) joined the guild"
    )
    private val promotedOrDemotedRegex = regexGroup.create(
        "promoted_or_demote",
        "^(?:\\[.+] )?(?<name>[a-zA-Z0-9_]+) was (?:promoted|demoted) from"
    )
    // this gets shown when you run a guild command and not in guild
    private val notInGuildRegex = regexGroup.create(
        "not_in_guild",
        "^You must be in a guild"
    )

    //region Guild list
    private val listGroup = regexGroup.group("list")

    private val guildListSeparator = listGroup.create(
        "separator",
        "-{20,}"
    )

    private val guildMemberRegex = listGroup.create(
        "member",
        """(?:\[[^]]+] )?(?<name>[a-zA-Z0-9_]+) ●"""
    )
    private val offlineMembersRegex = listGroup.create(
        "offline_members",
        """Offline Members: \d+"""
    )
    //endregion
    //endregion

    public val guildName: String?
        get() = GuildStorage.name
    public val members: Collection<GuildMember>
        get() = GuildStorage.members
    public val inGuild: Boolean
        get() = guildName != null || members.isNotEmpty()

    private val debug = debugToggle("guild_api", "Allows you to see what messages get detected by GuildAPI, and what they do")

    private var inGuildListMessage = false
    private var showingOfflineMembers = true
    private val currentGuildListMessageMembers = mutableSetOf<String>()
    private var lastGuildListMessage = Instant.DISTANT_PAST

    private fun finishGuildListMessage() {
        inGuildListMessage = false
        val showingOffline = this.showingOfflineMembers

        if (showingOffline) {
            debugMessage { "Replacing members in guild" }
            GuildStorage.replaceMembers(currentGuildListMessageMembers)
        } else {
            debugMessage { "Only adding members to guild" }
            GuildStorage.addMembersByNames(currentGuildListMessageMembers)
        }

        currentGuildListMessageMembers.clear()
        showingOfflineMembers = true
    }

    private fun handleInsideGuildListMessage(event: ChatReceivedEvent.Pre) {
        val text = event.text
        if (text.isBlank()) return

        if (offlineMembersRegex.matches(text)) {
            debugMessage { "Detected not showing offline members in guild list" }
            showingOfflineMembers = false
            return
        }

        val current = mutableSetOf<String>()
        guildMemberRegex.findAll(text, "name") { (name) ->
            current.add(name)
        }
        if (current.isEmpty()) return
        debugMessage { "Found ${current.size} guild members in message" }
        debugMessage { "Members: ${current.joinToString()}" }
        currentGuildListMessageMembers.addAll(current)
    }

    // replace this with the actual kotlin function when that gets added in kotlin 2.5
    private inline fun Boolean.onTrue(action: () -> Unit) {
        if (this) action()
    }

    private inline fun debugMessage(message: () -> String) = debugString(debug, message)


    @Subscription(receiveCancelled = true)
    private fun onChat(event: ChatReceivedEvent.Pre) {
        val text = event.text

        guildNameRegex.match(text, "name") { (name) ->
            GuildStorage.setGuildName(name)
            debugMessage { "Guild name: '$name', starting guild list message" }
            // the guild name is always the first message in the `/guild list` message
            lastGuildListMessage = currentInstant()
            inGuildListMessage = true
        }.onTrue { return }

        if (inGuildListMessage) {
            if (guildListSeparator.matches(text)) {
                debugMessage { "Ended guild list message" }
                finishGuildListMessage()
            } else handleInsideGuildListMessage(event)
            return
        }

        guildMessageRegex.findOrNull(event.component, "name") { (name) ->
            val username = name.stripped
            val uuid = CommonRegexes.getUuidFromViewProfile(name) ?: return@findOrNull
            val (_, updated) = GuildStorage.ensureMemberAdded(username, uuid)
            if (updated) debugMessage { "Added/Updated member: $name [uuid: $uuid]" }
            else debugMessage { "Member already exists: $name [uuid: $uuid]" }
            PlayerCacheStorage.updatePlayer(uuid, username)
        } ?: return

        findWhen(text) {
            case(guildOnlineJoinLeaveRegex, "name", "action") { (name, action) ->
                val uuid = PlayerCacheStorage.getPlayerUUID(name)
                val (member, updated) = GuildStorage.ensureMemberAdded(name, uuid)
                if (updated) debugMessage { "Added member: $name [uuid: ${member.uuid}]" }
                else debugMessage { "Member already exists: $name [uuid: ${member.uuid}]" }

                val joined = action == "joined"
                if (joined) GuildEvent.Join(member).post()
                else GuildEvent.Leave(member).post()

                debugMessage { "Online status update: $name ($action)" }
            }
            case(otherGuildJoinRegex, "name") { (name) ->
                val uuid = PlayerCacheStorage.getPlayerUUID(name)
                GuildStorage.ensureMemberAdded(name, uuid)
                debugMessage { "Added member: $name [uuid: $uuid]" }
            }
            case(otherGuildLeaveRegex, "name") { (name) ->
                val uuid = PlayerCacheStorage.getPlayerUUID(name)
                val member = GuildStorage.remove(name, uuid)
                if (member != null) debugMessage { "Removed member: $name [uuid: $uuid]" }
                else debugMessage { "Member already removed: $name [uuid: $uuid]" }
            }
            case(promotedOrDemotedRegex, "name") { (name) ->
                val uuid = PlayerCacheStorage.getPlayerUUID(name)
                val (member, updated) = GuildStorage.ensureMemberAdded(name, uuid)
                if (updated) debugMessage { "Added member: $name [uuid: ${member.uuid}]" }
                else debugMessage { "Member already added: $name [uuid: $uuid]" }
            }
            case(ownLeftGuild) {
                GuildStorage.reset()
                debugMessage { "Left guild" }
            }
            case(notInGuildRegex) {
                if (GuildStorage.reset()) {
                    debugMessage { "Deleted guild data" }
                }
            }
        }

    }

    @TimePassed("1s")
    @Subscription(TickEvent::class)
    private fun onTick() {
        // fallback in case the closing line of guild list message doesn't appear after 2 seconds
        if (inGuildListMessage && lastGuildListMessage.since() > 2.seconds) finishGuildListMessage()
    }

    @Subscription
    private fun onPlayerCacheUpdate(event: PlayerCacheUpdate) {
        GuildStorage.updateIfNeeded(event.uuid, event.name)
    }

    @Subscription
    private fun onCommand(event: RegisterSkyblockApiCommandsEvent) {
        event.register("guild") {
            thenCallback("name") {
                val name = guildName
                when {
                    !inGuild -> Text.sendDebug("Not in a guild")
                    name == null -> Text.sendDebug("Guild name unknown")
                    else -> Text.sendDebug("Guild name: '$name'")
                }
            }
            thenCallback("members") {
                val members = members
                when {
                    !inGuild -> Text.sendDebug("Not in a guild")
                    members.isEmpty() -> Text.sendDebug("No guild members known")
                    else -> {
                        McClient.clipboard = members.joinToString("\n", transform = GuildMember::name)
                        Text.sendDebug("Copied ${members.size} guild members to clipboard")
                    }
                }
            }
            thenCallback("reset") {
                GuildStorage.reset()
                Text.sendDebug("Reset guild")
            }
        }
    }

}
