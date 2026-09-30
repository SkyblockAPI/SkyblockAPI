package tech.thatgravyboat.skyblockapi.datagen

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import kotlin.io.path.Path

object RegexDumping {

    val shouldDump: Boolean
        get() = System.getProperty("skyblockapi.regexes.dumpEnabled")?.lowercase() == "true"

    val dumpPath: String?
        get() = System.getProperty("skyblockapi.regexes.dumpPath")

    fun tryDump() {
        if (!shouldDump) SkyBlockAPIDatagen.info("Not dumping regexes")
        val path = dumpPath ?: error("skyblockapi.regexes.dumpPath not set")
        Regexes.dumpRegexes(Path(path))
    }

}
