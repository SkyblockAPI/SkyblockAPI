package tech.thatgravyboat.skyblockapi.api.profile.party

import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerCacheStorage
import java.util.*

class PartyMember internal constructor(uuid: UUID?, role: PartyRole = PartyRole.MEMBER, online: Boolean = true) {

    internal constructor(name: String, role: PartyRole = PartyRole.MEMBER, online: Boolean = true) : this(null, role, online) {
        this.name = name
    }

    internal constructor(uuid: UUID, name: String, role: PartyRole = PartyRole.MEMBER, online: Boolean = true) : this(uuid, role, online) {
        this.name = name
    }

    var uuid: UUID? = uuid
        internal set

    var role: PartyRole = role
        internal set

    var name: String? = if (uuid != null) PlayerCacheStorage.getPlayerName(uuid) else null
        internal set

    var isOnline: Boolean = online
        internal set

    internal fun missingData(): Boolean = name != null

    override fun toString(): String = "PartyMember(uuid=${uuid.cleanString()}, name=${name.cleanString()}, role=$role, isOnline=$isOnline)"

    private fun Any?.cleanString(): String = this?.toString() ?: "Unknown"

}
