package tech.thatgravyboat.skyblockapi.api.events.location.mineshaft

import tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft.Corpse
import tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft.CorpseType
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

public abstract class CorpseEvent : SkyBlockEvent() {
    public data class Spawn(public val corpses: List<Corpse>) : CorpseEvent()
    public data class Looted(public val type: CorpseType, public val loot: Map<SkyBlockId, Int>, public val keyConsumed: Boolean) : CorpseEvent()
}
