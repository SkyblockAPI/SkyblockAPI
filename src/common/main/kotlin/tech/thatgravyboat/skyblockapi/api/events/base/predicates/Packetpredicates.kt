package tech.thatgravyboat.skyblockapi.api.events.base.predicates

import net.minecraft.network.protocol.Packet
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicate
import tech.thatgravyboat.skyblockapi.api.events.base.EventPredicateProvider
import tech.thatgravyboat.skyblockapi.api.events.level.PacketEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.getAnnotation
import java.lang.reflect.Method
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnlyPacket(
    vararg val packets: KClass<out Packet<*>>,
)

class OnlyPacketEventPredicate : EventPredicateProvider {
    override fun getPredicate(method: Method): EventPredicate? {
        val onlyIn = method.getAnnotation<OnlyPacket>() ?: return null
        return predicate@{ event, _ ->
            val event = event as? PacketEvent ?: return@predicate true

            return@predicate onlyIn.packets.contains(event.packet::class)
        }
    }
}
