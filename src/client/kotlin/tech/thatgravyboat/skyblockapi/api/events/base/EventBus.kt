package tech.thatgravyboat.skyblockapi.api.events.base

import java.lang.reflect.Method

class EventBus {

    private val listeners: MutableMap<Class<*>, EventListeners> = mutableMapOf()
    private val handlers: MutableMap<Class<*>, EventHandler<*>> = mutableMapOf()

    fun register(instance: Any) {
        instance.javaClass.declaredMethods.forEach { registerMethod(it, instance) }
    }

    inline fun <reified T : SkyBlockEvent> register(priority: Int = 0, receiveCancelled: Boolean = false, noinline callback: (T) -> Unit) {
        register(T::class.java, priority, receiveCancelled, callback = callback)
    }

    fun <T : SkyBlockEvent> register(type: Class<T>, priority: Int = 0, receiveCancelled: Boolean = false, callback: (T) -> Unit) {
        unregisterHandler(type)
        listeners.getOrPut(type) { EventListeners() }.addListener(callback, priority, receiveCancelled)
    }

    fun unregister(instance: Any) {
        instance.javaClass.declaredMethods.forEach(::unregisterMethod)
    }

    inline fun <reified T : SkyBlockEvent> unregister(noinline callback: (T) -> Unit) {
        unregister(T::class.java, callback = callback)
    }

    fun <T : SkyBlockEvent> unregister(type: Class<T>, callback: (T) -> Unit) {
        unregisterHandler(type)
        listeners.values.forEach { it.removeListener(callback) }
    }

    fun post(
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

    private fun unregisterMethod(method: Method) {
        val (_, event) = getEventData(method) ?: return
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
            else -> throw IllegalStateException("Expected method with zero or one parameters got %s".format(method.parameterCount))
        }
    }

    private fun getEventData(method: Method): EventData? {
        val options = method.getAnnotation(Subscription::class.java) ?: return null
        if (method.parameterCount == 0 && options.event.isNotEmpty()) {
            return EventData(options, options.event.toList().map { it.java })
        }
        if (method.parameterTypes.size != 1) return null
        return method.parameterTypes.firstOrNull()?.let { EventData(options, listOf(it)) }
    }

    data class EventData(
        val options: Subscription,
        val events: List<Class<*>>,
    )

    private fun unregisterHandler(clazz: Class<*>) = this.handlers.keys
        .filter { it.isAssignableFrom(clazz) }
        .forEach(this.handlers::remove)

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
