package tech.thatgravyboat.skyblockapi.api.profile.party

import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.minecraft.network.chat.ClickEvent
import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerCacheStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.PartyInfoEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.impl.events.HypixelEventHandler
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanPlayerName
import tech.thatgravyboat.skyblockapi.utils.regex.CommonRegexes
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroups
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.component.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.*

private const val MINIMUM_PARTY_INFO_DELAY = 1000 * 60 // 1 minute

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
        "^You have joined (?:\\[.+] )?(?<leader>[a-zA-Z0-9_]+)'s? party!$"
    )
    private val ownLeaveRegex = ownGroup.createList(
        "leave",
        " has disbanded the party!$",
        "^You have been kicked from the party by ",
        "^You left the party\\.",
        "^The party was disbanded because all invites expired and the party was empty\\.",
        "^You are not (?:currently )?in a party\\.",
    )

    private val otherJoinedRegex = otherGroup.create(
        "join",
        "^(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) joined the party\\.$"
    )
    private val otherInPartyRegex = otherGroup.create(
        "inparty",
        "^You'll be partying with: (?<members>.+)"
    )
    private val otherLeftRegexList = otherGroup.createList(
        "left",
        "^(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) has (?:left|been removed from) the party\\.",
        "^Kicked (?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) because they were offline\\.",
        "^(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) was removed from your party because they disconnected\\."
    )

    private val transferLeaveRegex = transferGroup.create(
        "leave",
        "^The party was transferred to (?:\\[.+] )?(?<leader>[a-zA-Z0-9_]+) because (?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) left"
    )
    private val transferRegex = transferGroup.create(
        "normal",
        "^The party was transferred to (?:\\[.+] )?(?<leader>[a-zA-Z0-9_]+) by (?:\\[.+] )?(?<mod>[a-zA-Z0-9_]+)"
    )

    private val listMembersRegex = chatGroup.create(
        "list",
        "^Party (?<role>Leader|Moderators|Members): (?<members>.+)"
    )

    private val partyFinderRegex = chatGroup.create(
        "partyfinder",
        "^Party Finder > (?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) joined the"
    )
    private val allInviteRegex = chatGroup.create(
        "allinvite",
        "(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) (?<state>enabled|disabled) All Invite"
    )

    private val partyMessageRegex = chatGroup.create(
        "message",
        "^Party > (?:\\[.+] )?(?<member>[a-zA-Z0-9_]+): "
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
    private var lastPartyInfoRequest: Long = 0L

    @Subscription
    fun onChat(event: ChatReceivedEvent) {
        val message = event.text
        ownJoinedRegex.findThenNull(message, "leader") { (leaderName) ->
            inParty = true
            val leader = PartyMember(leaderName, PartyRole.LEADER)
            this.leader = leader
            members = listOf(leader, ownPlayer())
        } ?: return
        otherJoinedRegex.findThenNull(message, "member") { (memberName) ->
            if (!inParty) {
                inParty = true
                val ownPlayer = ownPlayer(PartyRole.LEADER)
                this.leader = ownPlayer
                this.members = listOf(ownPlayer)
            }
            add(PartyMember(memberName))
        } ?: return
        otherInPartyRegex.findThenNull(message, "members") { (membersList) ->
            if (checkParty()) return@findThenNull
            for (name in membersList.split(",")) {
                add(PartyMember(name.cleanPlayerName()))
            }
        } ?: return
        for (regex in otherLeftRegexList) {
            regex.findThenNull(message, "member") { (member) ->
                if (checkParty()) return@findThenNull
                remove(member)
            } ?: return
        }
        transferLeaveRegex.findThenNull(message, "leader", "member") { (leaderName, memberName) ->
            if (checkParty()) return@findThenNull
            setRole(leaderName, PartyRole.LEADER)
            remove(memberName)
        } ?: return
        transferRegex.findThenNull(message, "leader", "mod") { (leaderName, modName) ->
            if (checkParty()) return@findThenNull
            setRole(leaderName, PartyRole.LEADER)
            setRole(modName, PartyRole.MOD)
        } ?: return
        for (regex in ownLeaveRegex) {
            if (regex.contains(message)) {
                reset()
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
        } ?: return
        partyFinderRegex.findThenNull(message, "member") { (member) ->
            if (checkParty()) return@findThenNull
            add(PartyMember(member))
        } ?: return
        allInviteRegex.findThenNull(message, "member", "state") { (member, state) ->
            if (checkParty()) return@findThenNull
            this.allInvite = state == "enabled"
            findPlayer(member)?.let { player ->
                if (player.role == PartyRole.MEMBER) player.role = PartyRole.MOD
            } ?: add(PartyMember(member, PartyRole.MOD))
        } ?: return
        partyMessageRegex.findThenNull(event.component, "member") { (member) ->
            if (checkParty()) return@findThenNull
            val clickEvent = member.style.clickEvent ?: return@findThenNull
            if (clickEvent.action != ClickEvent.Action.RUN_COMMAND) return@findThenNull
            val (uuidString) = CommonRegexes.viewProfileRegex.findGroups(clickEvent.value, "uuid") ?: return@findThenNull
            val uuid = UUID.fromString(uuidString)
            val name = member.stripped
            val hasUpdated = PlayerCacheStorage.updatePlayer(uuid, name)
            if (!hasUpdated) return@findThenNull
            val player = findPlayer(uuid)
            if (player != null) player.name = name
            else add(PartyMember(uuid, name))
        } ?: return
    }

    @Subscription
    fun onPartyInfo(event: PartyInfoEvent) {
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

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("party") {
                callback {
                    Text.of("[SkyBlockAPI] Copied Party Info to clipboard.") {
                        this.color = TextColor.YELLOW
                    }.send()
                    val string = buildList {
                        add("inParty: $inParty")
                        add("leader: $leader")
                        add("members: (${members.joinToString()})")
                        add("size: ${this@PartyAPI.size}")
                        add("allInvite: $allInvite")
                    }.joinToString("\n")

                    McClient.clipboard = string
                }
            }
        }
    }

    private fun checkParty(): Boolean {
        if (inParty) return false
        inParty = true
        return requestPartyInfo()
    }

    private fun requestPartyInfo(): Boolean {
        if (requestedPartyInfo) return false
        val current = System.currentTimeMillis()
        if (current - lastPartyInfoRequest < MINIMUM_PARTY_INFO_DELAY) return false
        requestedPartyInfo = HypixelEventHandler.requestPartyInfo()
        if (requestedPartyInfo) lastPartyInfoRequest = current
        return requestedPartyInfo
    }

    private fun add(member: PartyMember) {
        this.members += member
    }

    private fun remove(name: String) {
        this.members = members.filter { it.name == name }
    }

    private fun ownPlayer(role: PartyRole = PartyRole.MEMBER) = PartyMember(McPlayer.name, role)

    private fun findPlayer(name: String): PartyMember? = this.members.find { it.name == name }

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
