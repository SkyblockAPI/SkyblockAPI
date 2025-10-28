package tech.thatgravyboat.skyblockapi.api.events.dungeon

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class DungeonKeyPickedUpEvent(val key: String, val amount: Int) : SkyBlockEvent()
