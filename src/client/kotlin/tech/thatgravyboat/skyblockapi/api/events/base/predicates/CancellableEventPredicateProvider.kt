package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import java.lang.reflect.Method

class CancellableEventPredicateProvider : EventPredicateProvider {

    override fun getPredicate(method: Method): EventPredicate? {
        val subscription = method.getAnnotation<Subscription>() ?: return null
        return { event, _ -> !event.isCancelled || subscription.receiveCancelled }
    }

}
