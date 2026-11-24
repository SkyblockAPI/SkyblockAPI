package tech.thatgravyboat.skyblockapi.api.events.misc

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import java.util.UUID

/** Only gets fired when a new player cache entry is added */
public data class PlayerCacheUpdate(val uuid: UUID, val name: String) : SkyBlockEvent()
