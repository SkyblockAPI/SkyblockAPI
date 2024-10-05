package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.data.MayorData
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

class MayorUpdateEvent(val mayor: MayorData.Candidate, val minister: MayorData.Candidate?) : SkyblockEvent()
