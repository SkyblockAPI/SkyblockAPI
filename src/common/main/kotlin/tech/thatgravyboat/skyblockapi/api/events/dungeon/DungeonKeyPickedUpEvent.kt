package tech.thatgravyboat.skyblockapi.api.events.dungeon

import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonKey
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class DungeonKeyPickedUpEvent(val key: DungeonKey) : SkyBlockEvent()
