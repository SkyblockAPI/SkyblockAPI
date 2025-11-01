package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import net.fabricmc.fabric.api.util.TriState
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerDemon
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerMiniBoss
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerMob
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.events.entity.SlayerEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import tech.thatgravyboat.skyblockapi.utils.extentions.hasAnnotation
import java.lang.reflect.Method

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerType(
    vararg val value: SlayerType,
    val acceptMiniBosses: Boolean = false,
    val acceptDemons: Boolean = false,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerMiniBoss(
    vararg val value: SlayerMiniBoss,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerDemon(
    vararg val value: SlayerDemon,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerDemons

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerMiniBosses(
    val bigBoys: TriState = TriState.DEFAULT,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerBosses

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MustBeOwnedByPlayer

class SlayerEventPredicateProvider : EventPredicateProvider {

    override fun getPredicate(method: Method): EventPredicate? {
        val validTypes = mutableListOf<SlayerMob>()

        when {
            method.hasAnnotation<OnlySlayerType>() -> {
                val slayerTypeAnnotation = method.getAnnotation<OnlySlayerType>()!!
                if (slayerTypeAnnotation.acceptDemons) {
                    validTypes.addAll(SlayerDemon.entries.filter { slayerTypeAnnotation.value.contains(it.slayerType) })
                }

                if (slayerTypeAnnotation.acceptMiniBosses) {
                    validTypes.addAll(SlayerMiniBoss.entries.filter { slayerTypeAnnotation.value.contains(it.slayerType) })
                }
                validTypes.addAll(slayerTypeAnnotation.value)
            }

            method.hasAnnotation<OnlySlayerMiniBoss>() -> {
                validTypes.addAll(method.getAnnotation<OnlySlayerMiniBoss>()!!.value)
            }

            method.hasAnnotation<OnlySlayerDemon>() -> {
                validTypes.addAll(method.getAnnotation<OnlySlayerDemon>()!!.value)
            }

            method.hasAnnotation<OnlySlayerBosses>() -> validTypes.addAll(SlayerType.entries)
            method.hasAnnotation<OnlySlayerMiniBosses>() -> {
                val annotation = method.getAnnotation<OnlySlayerMiniBosses>()!!
                when (annotation.bigBoys) {
                    TriState.FALSE -> validTypes.addAll(SlayerMiniBoss.entries.filterNot { it.isBigBoy })
                    TriState.TRUE -> validTypes.addAll(SlayerMiniBoss.entries.filter { it.isBigBoy })
                    else -> validTypes.addAll(SlayerMiniBoss.entries)
                }
            }

            method.hasAnnotation<OnlySlayerDemons>() -> validTypes.addAll(SlayerDemon.entries)
        }

        val mustBeOwnedByPlayer = method.hasAnnotation<MustBeOwnedByPlayer>()
        if (!mustBeOwnedByPlayer && validTypes.isEmpty()) return null
        return predicate@{ event, _ ->
            if (event is SlayerEvent) {
                if (mustBeOwnedByPlayer && !event.slayerInfo.isOwnedByPlayer) {
                    return@predicate false
                }

                validTypes.contains(event.slayerInfo.type)
            } else false
        }
    }
}
