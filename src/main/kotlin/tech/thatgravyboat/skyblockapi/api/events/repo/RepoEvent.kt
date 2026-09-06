package tech.thatgravyboat.skyblockapi.api.events.repo

import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

sealed class RepoEvent : SkyBlockEvent() {

    /**
     * Triggers whenever the repo (re-)loads
     */
    data class Reload(val status: RepoStatus) : RepoEvent()

    /**
     * Triggers specifically when repo loads on boot
     */
    data class Status(val status: RepoStatus) : RepoEvent()
}
