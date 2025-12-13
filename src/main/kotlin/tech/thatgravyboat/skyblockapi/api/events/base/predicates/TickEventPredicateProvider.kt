package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import tech.thatgravyboat.skyblockapi.utils.extentions.parseDuration
import java.lang.reflect.Method

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TimePassed(

    /**
     * Duration formatted in the 2m 5s format
     */
    val duration: String
)


class TickEventPredicateProvider : EventPredicateProvider {

    override fun getPredicate(method: Method): EventPredicate? {
        val timePassed = method.getAnnotation<TimePassed>() ?: return null
        val duration = timePassed.duration.parseDuration() ?: error("Invalid duration provided for ${method.name}")
        val ticks = (duration.inWholeMilliseconds / 50).toInt()
        if (ticks <= 1) error("Duration provided for ${method.name} must be greater than 1 tick")
        return { _, count -> (count as? Int)?.rem(ticks) == 0  }
    }

}
