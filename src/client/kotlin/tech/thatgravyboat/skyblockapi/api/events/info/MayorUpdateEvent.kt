package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.data.Candidate
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

class MayorUpdateEvent(val mayor: Candidate, val minister: Candidate?) : SkyblockEvent()
