package tech.thatgravyboat.skyblockapi.api.events.base

import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.debug.DebugEvents
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

public class EventBus {

    private val listeners: MutableMap<Class<*>, EventListeners> = ConcurrentHashMap()
    private val handlers: MutableMap<Class<*>, EventHandler<*>> = ConcurrentHashMap()

    public fun register(instance: Any) {
        var clazz: Class<*>? = instance.javaClass
        while (clazz != null) {
            if (clazz == Any::class.java) break

            clazz.declaredMethods.forEach { registerMethod(it, instance) }
            clazz = clazz.superclass
        }
    }

    public inline fun <reified T : SkyBlockEvent> register(priority: Int = 0, receiveCancelled: Boolean = false, noinline callback: (T) -> Unit) {
        register(T::class.java, priority, receiveCancelled, callback = callback)
    }

    public fun <T : SkyBlockEvent> register(type: Class<T>, priority: Int = 0, receiveCancelled: Boolean = false, callback: (T) -> Unit) {
        unregisterHandler(type)
        listeners.getOrPut(type) { EventListeners() }.addListener(callback, priority, receiveCancelled)
    }

    public fun unregister(instance: Any) {
        var clazz: Class<*>? = instance.javaClass
        while (clazz != null) {
            if (clazz == Any::class.java) break
            clazz.declaredMethods.forEach { unregisterMethod(it, instance) }
            clazz = clazz.superclass
        }
    }

    public inline fun <reified T : SkyBlockEvent> unregister(noinline callback: (T) -> Unit) {
        unregister(T::class.java, callback = callback)
    }

    public fun <T : SkyBlockEvent> unregister(type: Class<T>, callback: (T) -> Unit) {
        unregisterHandler(type)
        listeners.values.forEach { it.removeListener(callback) }
    }

    // We don't provide an `instance` of the class here, so it doesn't check if the method passed is part of a super class
    public fun unregisterMethod(kFunction: KFunction<*>) {
        unregisterMethod(kFunction.javaMethod ?: return)
    }

    // We don't provide an `instance` of the class here, so it doesn't check if the method passed is part of a super class
    public fun unregisterMethod(method: Method) {
        val (_, event) = getEventData(method) ?: return
        event.forEach {
            unregisterMethodInternal(method, it)
        }
    }


    public fun post(
        event: SkyBlockEvent,
        context: Any? = null,
        onError: ((Throwable) -> Unit)? = null,
    ): Boolean = getHandler(event.javaClass).post(event, context, onError)

    @Suppress("UNCHECKED_CAST")
    private fun <T : SkyBlockEvent> getHandler(event: Class<T>): EventHandler<T> = handlers.getOrPut(event) {
        EventHandler(
            event,
            getEventClasses(event).mapNotNull { listeners[it] }.flatMap(EventListeners::getListeners),
        )
    } as EventHandler<T>

    private fun unregisterMethod(method: Method, instance: Any) {
        val (options, event) = getEventData(method) ?: return
        if (!options.inherited && method.declaringClass != instance.javaClass) return
        event.forEach {
            unregisterMethodInternal(method, it)
        }
    }

    private fun unregisterMethodInternal(method: Method, event: Class<*>) {
        if (!SkyBlockEvent::class.java.isAssignableFrom(event)) return
        unregisterHandler(event)
        listeners.values.forEach { it.removeListener(method) }
    }

    private fun registerMethod(method: Method, instance: Any) {
        val (options, events) = getEventData(method) ?: return
        if (!options.inherited && method.declaringClass != instance.javaClass) return
        DebugEvents.tryWarn(method)

        events.forEach {
            registerMethodInternal(method, instance, it, options)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerMethodInternal(method: Method, instance: Any, event: Class<*>, options: Subscription) {
        if (!SkyBlockEvent::class.java.isAssignableFrom(event)) return
        unregisterHandler(event)
        val listeners = listeners.getOrPut(event as Class<SkyBlockEvent>) { EventListeners() }
        when (method.parameterCount) {
            1 -> listeners.addListener(method, instance, options)
            0 -> listeners.addNoArgListener(method, instance, options)
            else -> McClient.throwInDev("Expected method with zero or one parameters got %s".format(method.parameterCount))
        }
    }

    private fun getEventData(method: Method): EventData? {
        val options = method.getAnnotation(Subscription::class.java) ?: return null
        if (method.parameterCount == 0 && options.event.isNotEmpty()) {
            return EventData(options, options.event.toList().map { it.java })
        }
        val singleEvent = method.parameterTypes.singleOrNull()
        if (singleEvent == null) {
            McClient.throwInDev("Expected exactly one parameter type in function ${method.name} in class ${method.declaringClass.name}")
            return null
        }
        return EventData(options, listOf(singleEvent))
    }

    public data class EventData(
        val options: Subscription,
        val events: List<Class<*>>,
    )

    private fun unregisterHandler(clazz: Class<*>) {
        this.handlers.keys.removeIf { it.isAssignableFrom(clazz) }
    }

    private fun getEventClasses(clazz: Class<*>): List<Class<*>> {
        val classes = mutableListOf<Class<*>>()
        classes.add(clazz)

        var current = clazz
        while (current.superclass != null) {
            val superClass = current.superclass
            if (superClass == SkyBlockEvent::class.java) break
            if (superClass == CancellableSkyBlockEvent::class.java) break
            classes.add(superClass)
            current = superClass
        }
        return classes
    }
}
