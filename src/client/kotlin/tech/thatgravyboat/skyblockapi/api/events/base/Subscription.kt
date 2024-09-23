package tech.thatgravyboat.skyblockapi.api.events.base

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Subscription(
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
