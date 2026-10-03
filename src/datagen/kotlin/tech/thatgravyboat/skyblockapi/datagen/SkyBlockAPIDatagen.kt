package tech.thatgravyboat.skyblockapi.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SkyBlockAPIDatagen : DataGeneratorEntrypoint, Logger by LoggerFactory.getLogger("SkyBlockAPIDatagen") {

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        RegexDumping.tryDump()
    }
}
