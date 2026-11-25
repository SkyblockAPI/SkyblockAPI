package tech.thatgravyboat.skyblockapi.api.events.location.hollows

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

public data class CrystalNucleusLootedEvent(public val loot: Map<SkyBlockId, Int>) : SkyBlockEvent()
