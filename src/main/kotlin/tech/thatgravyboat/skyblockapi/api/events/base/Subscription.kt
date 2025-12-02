package tech.thatgravyboat.skyblockapi.api.events.base

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Subscription(
    /**
     * The event that will be received, only is required if there are no parameters.
     */
    vararg val event: KClass<out SkyBlockEvent> = [],

    /**
     * If the method is in a super class of a registered instance the method will still invoke.
     */
    val inherited: Boolean = false,

    /**
     * The priority of when the event will be called, lower priority will be called first, see the companion object.
     */
    val priority: Int = 0,

    /**
     * If the event is cancelled & receiveCancelled is true, then the method will still invoke.
     */
    val receiveCancelled: Boolean = false,
) {

    companion object {
        const val HIGHEST = -2000000
        const val HIGH = -100000
        const val LOW = 100000
        const val LOWEST = 2000000
    }
}
