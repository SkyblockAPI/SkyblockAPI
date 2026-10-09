package tech.thatgravyboat.skyblockapi.api.events.party

import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.area.isle.kuudra.KuudraTier
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public class DungeonPartyFinderQueueEvent(
    public val floor: DungeonFloor,
    public val groupNote: String,
    public val dungeonLevelRequirement: Int,
    public val classLevelRequirement: Int
) : SkyBlockEvent()

public class KuudraPartyFinderQueueEvent(
    public val tier: KuudraTier,
    public val groupNote: String,
    public val combatLevelRequirement: Int
) : SkyBlockEvent()
