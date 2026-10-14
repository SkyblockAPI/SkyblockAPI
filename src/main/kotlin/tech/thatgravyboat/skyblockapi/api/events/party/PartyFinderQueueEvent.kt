package tech.thatgravyboat.skyblockapi.api.events.party

import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.area.isle.kuudra.KuudraTier
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public data class DungeonPartyFinderQueueEvent(
    val floor: DungeonFloor,
    val groupNote: String,
    val dungeonLevelRequirement: Int,
    val classLevelRequirement: Int
) : SkyBlockEvent()

public data class KuudraPartyFinderQueueEvent(
    val tier: KuudraTier,
    val groupNote: String,
    val combatLevelRequirement: Int
) : SkyBlockEvent()
