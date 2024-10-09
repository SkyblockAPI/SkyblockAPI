package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import java.lang.reflect.Method

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlyOnSkyBlock

class OnlyOnSkyBlockEventPredicateProvider : EventPredicateProvider {

    override fun getPredicate(method: Method): EventPredicate? {
        method.getAnnotation<OnlyOnSkyBlock>() ?: return null
        return { _, _ -> LocationAPI.isOnSkyblock }
    }
}
