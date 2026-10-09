package tech.thatgravyboat.skyblockapi.api.events.location.mineshaft

import tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft.Corpse
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public class CorpseSpawnEvent(public val corpses: List<Corpse>) : SkyBlockEvent()
