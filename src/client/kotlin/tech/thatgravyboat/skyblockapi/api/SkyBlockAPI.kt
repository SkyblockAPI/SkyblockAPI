package tech.thatgravyboat.skyblockapi.api

import com.mojang.logging.LogUtils
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.generated.Modules
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry

object SkyBlockAPI {

    @JvmStatic
    val eventBus = EventBus()

    internal val logger = LogUtils.getLogger()

    internal val isDebug get() = System.getProperty("skyblockapi.debug")?.lowercase() == "true"

    @JvmStatic
    internal fun init() {
        Modules.load()
    }

    @JvmStatic
    internal fun postInit() {
        DataTypesRegistry.load()
    }
}
