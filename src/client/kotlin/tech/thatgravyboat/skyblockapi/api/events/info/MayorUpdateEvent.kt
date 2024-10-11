package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.data.Candidate
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class MayorUpdateEvent(val mayor: Candidate, val minister: Candidate?) : SkyBlockEvent()
