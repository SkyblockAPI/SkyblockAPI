package tech.thatgravyboat.skyblockapi.api.events.base

import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.util.function.Consumer

internal class EventListeners {

    private val listeners: MutableList<Listener> = mutableListOf()

    fun removeListener(listener: Any) {
        listeners.removeIf { it.listener == listener }
    }

    fun <T> addListener(callback: (T) -> Unit, priority: Int, receiveCancelled: Boolean) {
        @Suppress("UNCHECKED_CAST")
        listeners.add(Listener(
            callback,
            { callback(it as T) },
            priority,
            receiveCancelled,
            EventPredicates(listOf { event, _ -> receiveCancelled || !event.isCancelled })
        ))
    }

    fun addListener(method: Method, instance: Any, options: Subscription) {
        val name = "${method.declaringClass.name}.${method.name}${
            method.parameterTypes.joinTo(
                StringBuilder(),
                prefix = "(",
                postfix = ")",
                separator = ", ",
                transform = Class<*>::getTypeName,
            )
        }"
        listeners.add(Listener(
            method,
            createEventConsumer(name, instance, method),
            options.priority,
            options.receiveCancelled,
            EventPredicates(method)
        ))
    }

    /**
     * Creates a consumer using LambdaMetafactory, this is the most efficient way to reflectively call
     * a method from within code.
     */
    @Suppress("UNCHECKED_CAST")
    private fun createEventConsumer(name: String, instance: Any, method: Method): Consumer<Any> {
        try {
            val handle = MethodHandles.lookup().unreflect(method)
            return LambdaMetafactory.metafactory(
                MethodHandles.lookup(),
                "accept",
                MethodType.methodType(Consumer::class.java, instance::class.java),
                MethodType.methodType(Nothing::class.javaPrimitiveType, Object::class.java),
                handle,
                MethodType.methodType(Nothing::class.javaPrimitiveType, method.parameterTypes[0]),
            ).target.bindTo(instance).invokeExact() as Consumer<Any>
        } catch (e: Throwable) {
            throw IllegalArgumentException("Method $name is not a valid consumer", e)
        }
    }

    fun getListeners(): List<Listener> = listeners

    class Listener(
        val listener: Any,
        val invoker: Consumer<Any>,
        val priority: Int,
        val receiveCancelled: Boolean,
        val predicate: EventPredicates,
    )
}
