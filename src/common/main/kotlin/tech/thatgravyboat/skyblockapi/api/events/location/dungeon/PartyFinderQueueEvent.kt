package tech.thatgravyboat.skyblockapi.api.events.location.dungeon

import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class PartyFinderQueueEvent(
    val floor: DungeonFloor,
    val groupNote: String,
    val dungeonLevelRequirement: Int,
    val classLevelRequirement: Int
) : SkyBlockEvent()
