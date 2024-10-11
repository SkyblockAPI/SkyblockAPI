package tech.thatgravyboat.skyblockapi.api.events.base

import java.lang.reflect.Method

class EventBus {

    private val listeners: MutableMap<Class<*>, EventListeners> = mutableMapOf()
    private val handlers: MutableMap<Class<*>, EventHandler<*>> = mutableMapOf()
    private var frozen = false

    fun register(instance: Any) {
        if (frozen) throw IllegalStateException("Cannot register events after the event bus has been frozen.")
        instance.javaClass.declaredMethods.forEach {
            registerMethod(it, instance)
        }
    }

    fun post(event: SkyBlockEvent, context: Any? = null): Boolean =
        getHandler(event.javaClass).post(event, context)

    fun freeze() {
        frozen = true
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : SkyBlockEvent> getHandler(event: Class<T>): EventHandler<T> = handlers.getOrPut(event) {
        EventHandler(
            event,
            getEventClasses(event).mapNotNull { listeners[it] }.flatMap(EventListeners::getListeners)
        )
    } as EventHandler<T>

    @Suppress("UNCHECKED_CAST")
    private fun registerMethod(method: Method, instance: Any) {
        if (method.parameterCount != 1) return
        val options = method.getAnnotation(Subscription::class.java) ?: return
        val event = method.parameterTypes[0]
        if (!SkyBlockEvent::class.java.isAssignableFrom(event)) return
        listeners.getOrPut(event as Class<SkyBlockEvent>) { EventListeners() }.addListener(method, instance, options)
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
