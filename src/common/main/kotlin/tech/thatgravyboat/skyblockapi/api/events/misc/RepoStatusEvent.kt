package tech.thatgravyboat.skyblockapi.api.events.misc

import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class RepoStatusEvent(val status: RepoStatus) : SkyBlockEvent()
