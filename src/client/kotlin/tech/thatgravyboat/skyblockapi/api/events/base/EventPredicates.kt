package tech.thatgravyboat.skyblockapi.api.events.base

import java.lang.reflect.Method
import java.util.ServiceLoader

typealias EventPredicate = (event: SkyblockEvent, context: Any?) -> Boolean

private val providers = ServiceLoader.load(EventPredicateProvider::class.java).toList()

internal class EventPredicates(method: Method) {

    private val predicates: List<EventPredicate> =
        providers.mapNotNull { it.getPredicate(method) }

    fun test(event: SkyblockEvent, context: Any?): Boolean =
        predicates.all { it(event, context) }
}

interface EventPredicateProvider {
    fun getPredicate(method: Method): EventPredicate?
}


