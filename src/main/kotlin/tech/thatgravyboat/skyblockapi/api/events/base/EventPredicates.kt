package tech.thatgravyboat.skyblockapi.api.events.base

import java.lang.reflect.Method
import java.util.ServiceLoader
import kotlin.reflect.KClass

/** Annotation added to event predicate annotations to indicate which classes it can be used on. */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class PredicateForEvents(vararg val event: KClass<out SkyBlockEvent>)

public typealias EventPredicate = (event: SkyBlockEvent, context: Any?) -> Boolean

private val providers = ServiceLoader.load(EventPredicateProvider::class.java).toList()

internal class EventPredicates(private val predicates: List<EventPredicate>) {

    constructor(method: Method) : this(providers.mapNotNull { it.getPredicate(method) })

    fun test(event: SkyBlockEvent, context: Any?): Boolean =
        predicates.all { it(event, context) }
}

public interface EventPredicateProvider {
    public fun getPredicate(method: Method): EventPredicate?
}


