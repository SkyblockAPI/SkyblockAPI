package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import java.lang.reflect.Method

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlyWidget(
    vararg val widgets: TabWidget
)

class TabWidgetEventPredicateProvider : EventPredicateProvider {

    override fun getPredicate(method: Method): EventPredicate? {
        val onlyIn = method.getAnnotation<OnlyWidget>() ?: return null
        val widgets = onlyIn.widgets.toSet()
        return { _, widget -> widget as? TabWidget in widgets }
    }
}
