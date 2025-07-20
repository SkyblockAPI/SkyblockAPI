package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import java.lang.reflect.Method

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class InventoryTitle(
    @Language("RegExp") vararg val title: String,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MustBeContainer

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class IgnoreFiller

class InventoryPredicates : EventPredicateProvider {
    override fun getPredicate(method: Method): EventPredicate? {
        val inventoryTitle = method.getAnnotation<InventoryTitle>()?.title?.map { Regex(it) }
        val disallowPlayerInventory = method.getAnnotation<MustBeContainer>() != null
        val ignoreFiller = method.getAnnotation<IgnoreFiller>() != null

        return predicate@{ event, _ ->
            if (inventoryTitle == null && !disallowPlayerInventory) {
                return@predicate true
            }

            if (event !is ContainerInitializedEvent && event !is InventoryChangeEvent) {
                return@predicate true
            }

            val (title, isPlayer) = when (event) {
                is InventoryChangeEvent -> event.title to event.isInPlayerInventory
                is ContainerInitializedEvent -> event.title to false
                else -> return@predicate true
            }

            val isFiller = (event as? InventoryChangeEvent)?.isSkyBlockFiller ?: false

            if (ignoreFiller && isFiller) return@predicate false
            if (inventoryTitle == null) return@predicate !isPlayer
            else return@predicate inventoryTitle.any { it.match(title) } && !isPlayer
        }
    }
}

