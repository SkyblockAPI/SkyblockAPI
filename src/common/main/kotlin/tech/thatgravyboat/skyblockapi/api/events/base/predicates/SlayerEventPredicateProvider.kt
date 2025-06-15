package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import net.fabricmc.fabric.api.util.TriState
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerDemon
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerMiniBoss
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerMob
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.events.entity.SlayerEvent
import tech.thatgravyboat.skyblockapi.utils.extensions.getAnnotation
import java.lang.reflect.Method

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerType(
    val value: Array<SlayerType>,
    val acceptMiniBosses: Boolean = false,
    val acceptDemons: Boolean = false,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerMiniBoss(
    val value: Array<SlayerMiniBoss>,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlySlayerDemon(
    val value: Array<SlayerDemon>,
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
        val mustBeOwnedByPlayer = method.isAnnotationPresent(MustBeOwnedByPlayer::class.java)

        when {
            method.isAnnotationPresent(OnlySlayerType::class.java) -> {
                val slayerTypeAnnotation = method.getAnnotation<OnlySlayerType>()
                if (slayerTypeAnnotation?.acceptDemons == true) {
                    validTypes.addAll(SlayerDemon.entries.filter { slayerTypeAnnotation.value.contains(it.slayerType) })
                }

                if (slayerTypeAnnotation?.acceptMiniBosses == true) {
                    validTypes.addAll(SlayerMiniBoss.entries.filter { slayerTypeAnnotation.value.contains(it.slayerType) })
                }
                slayerTypeAnnotation?.value?.let { validTypes.addAll(it) }
            }

            method.isAnnotationPresent(OnlySlayerMiniBoss::class.java) -> {
                method.getAnnotation<OnlySlayerMiniBoss>()?.value?.let { validTypes.addAll(it) }
            }

            method.isAnnotationPresent(OnlySlayerDemon::class.java) -> {
                method.getAnnotation<OnlySlayerDemon>()?.value?.let { validTypes.addAll(it) }
            }

            method.isAnnotationPresent(OnlySlayerBosses::class.java) -> validTypes.addAll(SlayerType.entries)
            method.isAnnotationPresent(OnlySlayerMiniBosses::class.java) -> {
                val annotation = method.getAnnotation<OnlySlayerMiniBosses>()
                when (annotation?.bigBoys) {
                    TriState.FALSE -> validTypes.addAll(SlayerMiniBoss.entries.filterNot { it.isBigBoy })
                    TriState.TRUE -> validTypes.addAll(SlayerMiniBoss.entries.filter { it.isBigBoy })
                    else -> validTypes.addAll(SlayerMiniBoss.entries)
                }
            }

            method.isAnnotationPresent(OnlySlayerDemons::class.java) -> validTypes.addAll(SlayerDemon.entries)

            else -> null
        } ?: return null

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
