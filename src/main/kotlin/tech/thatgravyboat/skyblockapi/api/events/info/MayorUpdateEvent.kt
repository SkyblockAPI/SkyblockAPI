package tech.thatgravyboat.skyblockapi.api.events.info

//? < 26.1 {
/*
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.Candidate
 *///? }
import tech.thatgravyboat.skyblockapi.api.data.MayorCandidate
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class MayorChangeEvent(val mayor: MayorCandidate, val minister: MayorCandidate?) : SkyBlockEvent()

//? < 26.1 {
/*@RemoveNextVersion(ReplaceWith("MayorChangeEvent"))
class MayorUpdateEvent(val mayor: Candidate, val minister: Candidate?) : SkyBlockEvent()
*///? }
