package tech.thatgravyboat.skyblockapi.api.events.base

import me.owdding.ktmodules.AutoCollect

@AutoCollect("DevModules")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class DevModule
