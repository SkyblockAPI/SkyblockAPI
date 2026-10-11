package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.data.MayorCandidate
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public class MayorChangeEvent(public val mayor: MayorCandidate, public val minister: MayorCandidate?) : SkyBlockEvent()
