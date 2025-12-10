package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.Candidate
import tech.thatgravyboat.skyblockapi.api.data.MayorCandidate
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class MayorChangeEvent(val mayor: MayorCandidate, val minister: MayorCandidate?) : SkyBlockEvent()

@RemoveNextVersion(ReplaceWith("MayorChangeEvent"))
class MayorUpdateEvent(val mayor: Candidate, val minister: Candidate?) : SkyBlockEvent()
