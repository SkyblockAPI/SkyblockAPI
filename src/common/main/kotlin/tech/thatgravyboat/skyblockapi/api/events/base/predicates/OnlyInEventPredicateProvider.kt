package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import tech.thatgravyboat.skyblockapi.utils.extentions.hasAnnotation
import tech.thatgravyboat.skyblockapi.utils.extentions.toEnumSet
import java.lang.reflect.Method

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlyIn(
    vararg val islands: SkyBlockIsland,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlyNonGuest

class OnlyInEventPredicateProvider : EventPredicateProvider {

    override fun getPredicate(method: Method): EventPredicate? {
        val onlyIn = method.getAnnotation<OnlyIn>()
        val onlyNonGuest = method.hasAnnotation<OnlyNonGuest>()
        if (onlyIn == null && !onlyNonGuest) return null
        if (onlyIn == null) {
            return { _, _ -> !LocationAPI.isGuest }
        }
        val islands = onlyIn.islands.toEnumSet()
        if (!onlyNonGuest) {
            return { _, _ -> islands.contains(LocationAPI.island) }
        }
        return { _, _ -> !LocationAPI.isGuest && islands.contains(LocationAPI.island) }
    }
}
