package tech.thatgravyboat.skyblockapi.api.events.base

/**
 * An annotation to mark events that are not called on the main thread.
 * Usually in the network thread.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class OffThreadEvent()
