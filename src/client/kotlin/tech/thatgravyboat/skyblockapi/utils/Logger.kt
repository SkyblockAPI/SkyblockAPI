package tech.thatgravyboat.skyblockapi.utils

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

object Logger {

    fun debug(message: String, vararg args: Any) {
        if (!SkyBlockAPI.isDebug) return
        warn("[DEBUG] $message", *args)
    }

    fun info(message: String, vararg args: Any) {
        SkyBlockAPI.logger.info(message, *args)
    }

    fun warn(message: String, vararg args: Any) {
        SkyBlockAPI.logger.warn(message, *args)
    }

    fun error(message: String, vararg args: Any) {
        SkyBlockAPI.logger.error(message, *args)
    }
}
