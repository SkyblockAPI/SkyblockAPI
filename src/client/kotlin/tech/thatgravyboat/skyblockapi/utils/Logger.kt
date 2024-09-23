package tech.thatgravyboat.skyblockapi.utils

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

object Logger {

    private val isDebug = System.getProperty("skyblockapi.debug")?.toBoolean() ?: false

    fun debug(message: String, vararg args: Any) {
        if (!isDebug) return
        warn("[DEBUG] $message", *args)
    }

    fun info(message: String, vararg args: Any) {
        SkyBlockAPI.logger.info(message, *args)
    }

    fun warn(message: String, vararg args: Any) {
        SkyBlockAPI.logger.warn(message, *args)
    }
}
