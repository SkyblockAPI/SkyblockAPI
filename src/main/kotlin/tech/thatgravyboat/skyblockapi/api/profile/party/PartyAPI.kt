package tech.thatgravyboat.skyblockapi.api.profile.party

import me.owdding.ktmodules.Module
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerCacheStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.PartyInfoEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.impl.events.HypixelEventHandler
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanPlayerName
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.regex.CommonRegexes
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.component.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private val MINIMUM_PARTY_INFO_DELAY = 1.minutes

internal typealias PartyRole = ClientboundPartyInfoPacket.PartyRole

@Module
object PartyAPI {

    //region Regex
    private val chatGroup = RegexGroup.CHAT.group("party")
    private val ownGroup = chatGroup.group("own")
    private val otherGroup = chatGroup.group("other")
    private val transferGroup = chatGroup.group("transfer")

    private val ownJoinedRegex = ownGroup.create(
        "join",
        "^You have joined (?:\\[.+] )?(?<leader>[a-zA-Z0-9_]+)'s? party!$",
    )
    private val ownLeaveRegex = ownGroup.createList(
        "leave",
        " has disbanded the party!$",
        "^You have been kicked from the party by ",
        "^You left the party\\.",
        "^The party was disbanded because all invites expired and the party was empty\\.",
        "^You are not (?:currently )?in a party\\.",
        "^The party was disbanded because the party leader disconnected\\.",
    )

    private val otherJoinedRegex = otherGroup.create(
        "join",
        "^(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) joined the party\\.$",
    )
    private val otherInPartyRegex = otherGroup.create(
        "inparty",
        "^You'll be partying with: (?<members>.+)",
    )
    private val otherLeftRegexList = otherGroup.createList(
        "left",
        "^(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) has (?:left|been removed from) the party\\.",
        "^Kicked (?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) because they were offline\\.",
        "^(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) was removed from your party because they disconnected\\.",
    )

    private val transferLeaveRegex = transferGroup.create(
        "leave",
        "^The party was transferred to (?:\\[.+] )?(?<leader>[a-zA-Z0-9_]+) because (?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) left",
    )
    private val transferRegex = transferGroup.create(
        "normal",
        "^The party was transferred to (?:\\[.+] )?(?<leader>[a-zA-Z0-9_]+) by (?:\\[.+] )?(?<mod>[a-zA-Z0-9_]+)",
    )

    private val listMembersRegex = chatGroup.create(
        "list",
        "^Party (?<role>Leader|Moderators|Members): (?<members>.+)",
    )

    private val partyFinderRegex = chatGroup.create(
        "partyfinder",
        "^Party Finder > (?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) joined the",
    )
    private val allInviteRegex = chatGroup.create(
        "allinvite",
        "(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) (?<state>enabled|disabled) All Invite",
    )

    private val partyMessageRegex = chatGroup.create(
        "message",
        "^Party > (?:\\[.+] )?(?<member>[a-zA-Z0-9_]+): ",
    ).toComponentRegex()
    //endregion

    var inParty: Boolean = false
        private set

    var leader: PartyMember? = null
        private set

    var members: List<PartyMember> = emptyList()
        private set

    val size: Int get() = members.size

    var allInvite: Boolean = false
        private set

    private var requestedPartyInfo: Boolean = false
    private var lastPartyInfoRequest: Instant = Instant.DISTANT_PAST

    private val debug by debugToggle("party_api", "Allows you to see what messages get detected by PartyAPI, and what they modify.")

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        val message = event.text
        ownJoinedRegex.findThenNull(message, "leader") { (leaderName) ->
            inParty = true
            val leader = PartyMember(leaderName, PartyRole.LEADER)
            this.leader = leader
            members = listOf(leader, ownPlayer())
            debugMessage { "Joined party (L: $leaderName)" }
        } ?: return
        otherJoinedRegex.findThenNull(message, "member") { (memberName) ->
            if (!inParty) {
                inParty = true
                val ownPlayer = ownPlayer(PartyRole.LEADER)
                this.leader = ownPlayer
                this.members = listOf(ownPlayer)
                debugMessage { "Detected party (L: assumed own)" }
            }
            add(PartyMember(memberName))
            debugMessage { "Member joined: $memberName" }
        } ?: return
        otherInPartyRegex.findThenNull(message, "members") { (membersList) ->
            if (checkParty()) return@findThenNull
            membersList.split(",").map { it.cleanPlayerName() }.onEach { name ->
                add(PartyMember(name))
            }.let {
                debugMessage { "Members in party: (${it.joinToString()})" }
            }
        } ?: return
        for (regex in otherLeftRegexList) {
            regex.findThenNull(message, "member") { (member) ->
                if (checkParty()) return@findThenNull
                remove(member)
                debugMessage { "Member left: $member" }
            } ?: return
        }
        transferLeaveRegex.findThenNull(message, "leader", "member") { (leaderName, memberName) ->
            if (checkParty()) return@findThenNull
            setRole(leaderName, PartyRole.LEADER)
            remove(memberName)
            debugMessage { "Party transferred to $leaderName because $memberName left" }
        } ?: return
        transferRegex.findThenNull(message, "leader", "mod") { (leaderName, modName) ->
            if (checkParty()) return@findThenNull
            setRole(leaderName, PartyRole.LEADER)
            setRole(modName, PartyRole.MOD)
            debugMessage { "Party transferred to $leaderName by $modName" }
        } ?: return
        for (regex in ownLeaveRegex) {
            if (regex.contains(message)) {
                reset()
                debugMessage { "Left party" }
                return
            }
        }
        listMembersRegex.findThenNull(message, "role", "members") { (role, membersList) ->
            val partyRole = when (role) {
                "Leader" -> {
                    this.members = emptyList()
                    PartyRole.LEADER
                }

                "Moderators" -> PartyRole.MOD

                else -> PartyRole.MEMBER
            }
            for (name in membersList.split("●")) {
                if (name.isBlank()) continue
                val member = PartyMember(name.cleanPlayerName(), partyRole)
                add(member)
                if (partyRole == PartyRole.LEADER) this.leader = member
            }
            debugMessage { "Updated party from members list" }
        } ?: return
        partyFinderRegex.findThenNull(message, "member") { (member) ->
            if (checkParty()) return@findThenNull
            add(PartyMember(member))
            debugMessage { "Member joined from Party Finder: $member" }
        } ?: return
        allInviteRegex.findThenNull(message, "member", "state") { (member, state) ->
            if (checkParty()) return@findThenNull
            this.allInvite = state == "enabled"
            findPlayer(member)?.let { player ->
                if (player.role == PartyRole.MEMBER) {
                    player.role = PartyRole.MOD
                    debugMessage { "Member $member detected as MOD" }
                } else {
                    debugMessage { "Member $member is already ${player.role}" }
                }
            } ?: run {
                add(PartyMember(member, PartyRole.MOD))
                debugMessage { "Adding $member as MOD" }
            }
        } ?: return
        partyMessageRegex.findThenNull(event.component, "member") { (member) ->
            if (checkParty()) return@findThenNull
            val uuid = CommonRegexes.getUuidFromViewProfile(member) ?: return@findThenNull
            val name = member.stripped
            val hasUpdated = PlayerCacheStorage.updatePlayer(uuid, name)
            if (!hasUpdated) return@findThenNull
            val player = findPlayer(uuid) ?: findPlayer(name)
            if (player != null) {
                player.uuid = uuid
                player.name = name
                debugMessage { "Updated player name: $name ($uuid)" }
            } else {
                add(PartyMember(uuid, name))
                debugMessage { "Added missing player: $name ($uuid)" }
            }
        } ?: return
    }

    @Subscription
    fun onPartyInfo(event: PartyInfoEvent) {
        debugMessage { "Updated from packet" }
        this.requestedPartyInfo = false
        if (!event.inParty) return reset()
        this.inParty = true

        if (event.members.size == 1) {
            val ownPlayer = ownPlayer(PartyRole.LEADER)
            leader = ownPlayer
            members = listOf(ownPlayer)
            return
        }

        members = buildList {
            for ((uuid, player) in event.members) {
                val member = PartyMember(uuid, player.role)
                if (player.role == PartyRole.LEADER) leader = member
                this.add(member)
            }
        }
    }

    private fun debugMessage(msg: () -> String) {
        if (debug) Text.sendDebug("PartyAPI: ${msg()}")
    }

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi party") {
            callback {
                val string = buildList {
                    add("inParty: $inParty")
                    add("leader: $leader")
                    add("members: (${members.joinToString()})")
                    add("size: ${this@PartyAPI.size}")
                    add("allInvite: $allInvite")
                }.joinToString("\n")
                McClient.clipboard = string
                Text.sendDebug("Copied Party Info to clipboard.")
            }
        }
    }

    private fun checkParty(): Boolean {
        if (inParty) return false
        debugMessage { "Party check failed" }
        inParty = true
        return requestPartyInfo()
    }

    private fun requestPartyInfo(): Boolean {
        if (requestedPartyInfo) return false
        if (lastPartyInfoRequest.since() < MINIMUM_PARTY_INFO_DELAY) return false
        requestedPartyInfo = HypixelEventHandler.requestPartyInfo()
        if (requestedPartyInfo) lastPartyInfoRequest = currentInstant()
        return requestedPartyInfo
    }

    private fun add(member: PartyMember) {
        this.members += member
    }

    private fun remove(name: String) {
        this.members = members.filterNot { it.name.equals(name, ignoreCase = true) }
    }

    private fun ownPlayer(role: PartyRole = PartyRole.MEMBER) = PartyMember(McPlayer.name, role)

    private fun findPlayer(name: String): PartyMember? = this.members.find { it.name.equals(name, ignoreCase = true) }

    private fun findPlayer(uuid: UUID): PartyMember? = this.members.find { it.uuid == uuid }

    private fun setRole(name: String, role: PartyRole) {
        findPlayer(name)?.also { it.role = role } ?: add(PartyMember(name, role))
    }

    private fun reset() {
        inParty = false
        leader = null
        members = emptyList()
        allInvite = false
        requestedPartyInfo = false
    }
}
