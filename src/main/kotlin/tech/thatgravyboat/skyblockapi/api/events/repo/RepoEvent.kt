package tech.thatgravyboat.skyblockapi.api.events.repo

import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public sealed class RepoEvent : SkyBlockEvent() {

    /**
     * Triggers whenever the repo (re-)loads
     */
    public data class Reload(val status: RepoStatus) : RepoEvent()

    /**
     * Triggers specifically when repo loads on boot
     */
    public data class Status(val status: RepoStatus) : RepoEvent()
}
