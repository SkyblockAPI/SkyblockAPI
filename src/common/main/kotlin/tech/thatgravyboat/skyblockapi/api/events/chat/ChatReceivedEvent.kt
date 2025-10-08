package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

abstract class ChatReceivedEvent : SkyBlockEvent() {

    abstract val component: Component

    val text: String get() = component.stripped
    val coloredText: String get() = component.string

    private val onError: ((Throwable) -> Unit)? = if (McClient.isDev) null else {
        { SkyBlockAPI.logger.error("Error posting ChatReceivedEvent", it) }
    }

    class Pre(override val component: Component) : ChatReceivedEvent(), Cancellable
    class Post(override var component: Component, var id: String? = null) : ChatReceivedEvent()

    override fun post(bus: EventBus) = bus.post(this, null, onError)
}
