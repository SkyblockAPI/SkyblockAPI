package tech.thatgravyboat.skyblockapi.api.profile.party

import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerCacheStorage
import java.util.*

class PartyMember(val uuid: UUID?, role: PartyRole = PartyRole.MEMBER) {

    constructor(name: String, role: PartyRole = PartyRole.MEMBER) : this(null, role) {
        this.name = name
    }

    constructor(uuid: UUID, name: String, role: PartyRole = PartyRole.MEMBER) : this (uuid, role) {
        this.name = name
    }

    var role: PartyRole = role
        internal set

    var name: String? = if (uuid != null) PlayerCacheStorage.getPlayerName(uuid) else null
        internal set

    internal fun missingData(): Boolean = name != null

    override fun toString(): String = "PartyMember(uuid=${uuid.cleanString()}, name=${name.cleanString()}, role=$role)"

    private fun Any?.cleanString(): String = this?.toString() ?: "Unknown"

}
