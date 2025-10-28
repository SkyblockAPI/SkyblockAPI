package tech.thatgravyboat.skyblockapi.api.events.party

import tech.thatgravyboat.skyblockapi.api.area.isle.kuudra.KuudraTier
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class KuudraPartyFinderQueueEvent(
    val tier: KuudraTier,
    val groupNote: String,
    val combatLevelRequirement: Int
) : SkyBlockEvent()
