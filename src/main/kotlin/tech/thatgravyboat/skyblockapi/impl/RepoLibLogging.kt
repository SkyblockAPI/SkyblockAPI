package tech.thatgravyboat.skyblockapi.impl

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.repolib.api.RepoLibLogger
import tech.thatgravyboat.skyblockapi.utils.debugToggle

object RepoLibLogging : RepoLibLogger, Logger by LoggerFactory.getLogger("Repo-Lib") {
    val enableDebugLogging by debugToggle("repo_lib_debug_logging")

    private inline fun updateLogLevel(runnable: () -> Unit) {
        if (enableDebugLogging) {
            Configurator.setLevel(name, Level.TRACE)
        } else {
            Configurator.setLevel(name, Level.INFO)
        }

        runnable()
    }

    override fun info0(p0: String) = updateLogLevel { info(p0) }
    override fun debug0(p0: String) = updateLogLevel { debug(p0) }
    override fun trace0(p0: String) = updateLogLevel { trace(p0) }
    override fun error0(p0: String) = updateLogLevel { error(p0) }
    override fun warn0(p0: String) = updateLogLevel { warn(p0) }
    override fun error0(p0: String, p1: Throwable) = updateLogLevel { error(p0, p1) }
}
