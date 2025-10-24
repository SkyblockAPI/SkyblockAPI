package tech.thatgravyboat.skyblockapi.api.events.dungeon

import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class DungeonEnterEvent(val floor: DungeonFloor) : SkyBlockEvent()
