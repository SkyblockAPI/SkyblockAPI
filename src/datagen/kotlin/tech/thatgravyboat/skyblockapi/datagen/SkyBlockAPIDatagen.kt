package tech.thatgravyboat.skyblockapi.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import kotlin.io.path.Path

object SkyBlockAPIDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val path = System.getProperty("skyblockapi.regexes.dumpPath") ?: error("skyblockapi.regexes.dumpPath not set")
        Regexes.dumpRegexes(Path(path))
    }
}
