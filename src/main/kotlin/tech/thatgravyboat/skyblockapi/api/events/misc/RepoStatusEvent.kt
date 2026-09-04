package tech.thatgravyboat.skyblockapi.api.events.misc

import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

@Deprecated("Use RepoEvent.Status instead", ReplaceWith("tech.thatgravyboat.skyblockapi.api.events.repo.RepoEvent.Status"))
data class RepoStatusEvent(val status: RepoStatus) : SkyBlockEvent()
