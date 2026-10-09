package tech.thatgravyboat.skyblockapi.api.events.dungeon

import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public class DungeonStartEvent(val floor: DungeonFloor) : SkyBlockEvent()
