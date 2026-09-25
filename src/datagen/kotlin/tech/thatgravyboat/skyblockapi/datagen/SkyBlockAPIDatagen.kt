package tech.thatgravyboat.skyblockapi.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import java.io.File

object SkyBlockAPIDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val path = "/home/empa/IdeaProjects/SkyblockAPI/versions/26.3/build/regexes/regexes.json"

        val file = File(path)
        file.parentFile?.mkdirs()
        Regexes.dumpRegexes(file.toPath())
    }


}
