package tech.thatgravyboat.skyblockapi.api

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

internal class SkyblockAPIModLoader : ModInitializer {
    override fun onInitialize() {
        Regexes.load()
        SkyBlockAPI.init()
        ClientLifecycleEvents.CLIENT_STARTED.register {
            SkyBlockAPI.postInit()
        }
    }
}
