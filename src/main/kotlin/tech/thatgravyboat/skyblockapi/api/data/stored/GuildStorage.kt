package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.GuildData
import tech.thatgravyboat.skyblockapi.api.data.StoredPlayerData
import tech.thatgravyboat.skyblockapi.api.profile.guild.GuildMember
import tech.thatgravyboat.skyblockapi.utils.extentions.replaceWith
import java.util.UUID
import kotlin.contracts.contract

internal object GuildStorage {

    private val STORAGE = StoredPlayerData<GuildData>(
        file = "guild",
        differentAlphaData = false,
    )

    private inline val data get() = STORAGE.get()

    val members: Set<GuildMember>
        field: MutableSet<GuildMember> = data.members

    var name: String?
        get() = data.name
        private set(value) = STORAGE.edit {
            if (data.name == value) return
            data.name = value
        }

    fun setGuildName(newName: String) {
        if (newName != this.name) {
            reset()
            this.name = newName
        }
    }

    fun inGuild(uuid: UUID): Boolean = members.any { it.uuid == uuid }
    fun inGuild(name: String): Boolean = members.any { it.name == name }

    fun replaceMembers(names: Collection<String>) {
        val new = names.withUUID().mapTo(mutableSetOf()) { (name, uuid) ->
            val existing = getMember(name, uuid)
            when {
                existing == null -> GuildMember(name, uuid)
                existing.name == name && existing.uuid == uuid -> existing
                else -> existing.copy(name = name, uuid = uuid)
            }
        }
        val members = data.members
        if (new == members) return

        members.replaceWith(new)
        save()
    }

    fun byUUID(uuid: UUID?): GuildMember? {
        contract {
            returnsNotNull() implies (uuid != null)
        }
        if (uuid == null) return null
        return members.find { it.uuid == uuid }
    }
    fun byName(name: String): GuildMember? = members.find { it.name == name }

    private fun getMember(name: String, uuid: UUID?): GuildMember? = byUUID(uuid) ?: byName(name)

    /** Returns the removed member, of null if none was removed */
    fun remove(name: String, uuid: UUID?): GuildMember? {
        val member = byUUID(uuid) ?: byName(name) ?: return null
        data.members.remove(member)
        save()
        return member
    }

    /** The [Boolean] in the pair is true if it was added/updated */
    fun ensureMemberAdded(name: String, uuid: UUID? = null): Pair<GuildMember, Boolean> {
        val members = data.members
        if (uuid != null) {
            val member = getMember(name, uuid)
            if (member == null) {
                val new = GuildMember(name, uuid)
                members.add(new)
                save()
                return new to true
            }
            if (member.uuid == uuid && member.name == name) return member to false
            members.remove(member)
            val new = member.copy(uuid = uuid, name = name)
            members.add(new)
            save()
            return new to true
        }
        val member = byName(name)
        if (member != null) return member to false
        val new = GuildMember(name)
        members.add(new)
        save()
        return new to true
    }

    fun addMembersByNames(names: Collection<String>) {
        names.withUUID().forEach { (name, uuid) ->
            ensureMemberAdded(name, uuid)
        }
    }

    fun updateIfNeeded(uuid: UUID, name: String) {
        val members = data.members
        val member = byUUID(uuid) ?: byName(name) ?: return

        if (member.name == name && member.uuid == uuid) return
        members.remove(member)
        members.add(member.copy(name = name, uuid = uuid))
        save()
    }

    fun reset(): Boolean = STORAGE.delete()

    fun save() = STORAGE.save()

    private fun Collection<String>.withUUID(): Map<String, UUID?> =
        associateWith { name ->
            PlayerCacheStorage.getPlayerUUID(name) ?: byName(name)?.uuid
        }

}
