package tech.thatgravyboat.skyblockapi.modules

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Module(val devOnly: Boolean = false)
