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
        onError: ((Throwable) -> Unit)? = null
    ): Boolean = getHandler(event.javaClass).post(event, context, onError)

    @Suppress("UNCHECKED_CAST")
    private fun <T : SkyBlockEvent> getHandler(event: Class<T>): EventHandler<T> = handlers.getOrPut(event) {
        EventHandler(
            event,
            getEventClasses(event).mapNotNull { listeners[it] }.flatMap(EventListeners::getListeners)
        )
    } as EventHandler<T>

    private fun unregisterMethod(method: Method) {
        if (method.parameterCount != 1) return
        method.getAnnotation(Subscription::class.java) ?: return
        val event = method.parameterTypes[0]
        if (!SkyBlockEvent::class.java.isAssignableFrom(event)) return
        unregisterHandler(event)
        listeners.values.forEach { it.removeListener(method) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerMethod(method: Method, instance: Any) {
        if (method.parameterCount != 1) return
        val options = method.getAnnotation(Subscription::class.java) ?: return
        val event = method.parameterTypes[0]
        if (!SkyBlockEvent::class.java.isAssignableFrom(event)) return
        unregisterHandler(event)
        listeners.getOrPut(event as Class<SkyBlockEvent>) { EventListeners() }.addListener(method, instance, options)
    }

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
