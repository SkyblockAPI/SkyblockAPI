package tech.thatgravyboat.skyblockapi.api

import net.fabricmc.api.ModInitializer
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

public class SkyblockAPIModLoader : ModInitializer {
    override fun onInitialize() {
        Regexes.load()
        SkyBlockAPI.init()
        SkyBlockAPI.postInit()
    }
}
