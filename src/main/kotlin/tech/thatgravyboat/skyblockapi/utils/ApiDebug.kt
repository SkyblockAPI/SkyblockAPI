package tech.thatgravyboat.skyblockapi.utils

import me.owdding.ktmodules.AutoCollect

@AutoCollect
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class ApiDebug(
    val name: String,
    val commandName: String = "<default>"
)
