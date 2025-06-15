package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.extensions.getAnnotation
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

class InventoryPredicates : EventPredicateProvider {
    override fun getPredicate(method: Method): EventPredicate? {
        val inventoryTitle = method.getAnnotation<InventoryTitle>()?.title?.map { Regex(it) }
        val disallowPlayerInventory = method.getAnnotation<MustBeContainer>() != null

        if (inventoryTitle == null && !disallowPlayerInventory) {
            return null
        }

        return predicate@{ event, _ ->
            if (event !is ContainerInitializedEvent && event !is InventoryChangeEvent) {
                return@predicate true
            }

            val (title, isPlayer) = when (event) {
                is InventoryChangeEvent -> event.title to event.isInPlayerInventory
                is ContainerInitializedEvent -> event.title to false
                else -> return@predicate true
            }

            if (inventoryTitle == null) return@predicate !isPlayer
            else return@predicate inventoryTitle.any { it.match(title) } && !isPlayer
        }
    }
}
