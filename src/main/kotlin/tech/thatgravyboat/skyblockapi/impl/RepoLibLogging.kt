package tech.thatgravyboat.skyblockapi.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.repolib.api.RepoLibLogger
import tech.thatgravyboat.skyblockapi.utils.debugToggle

public object RepoLibLogging : RepoLibLogger, Logger by LoggerFactory.getLogger("Repo-Lib") {
    public val enableDebugLogging: Boolean by debugToggle("repo_lib_debug_logging")

    private inline fun updateLogLevel(value: String, runnable: (String) -> Unit, debugLogger: (String) -> Unit = ::info) {
        if (enableDebugLogging) {
            debugLogger(value)
        } else {
            runnable(value)
        }
    }

    override fun info0(p0: String): Unit = updateLogLevel(p0, this::info)
    override fun debug0(p0: String): Unit = updateLogLevel(p0, this::debug)
    override fun trace0(p0: String): Unit = updateLogLevel(p0, this::trace)
    override fun error0(p0: String): Unit = updateLogLevel(p0, this::error, this::error)
    override fun warn0(p0: String): Unit = updateLogLevel(p0, this::warn, this::warn)
    override fun error0(p0: String, p1: Throwable): Unit = error(p0, p1)
}
