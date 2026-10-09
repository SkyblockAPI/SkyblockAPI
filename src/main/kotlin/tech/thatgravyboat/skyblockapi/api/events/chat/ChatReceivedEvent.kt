package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public abstract class ChatReceivedEvent : SkyBlockEvent() {

    public abstract val component: Component

    public open val text: String get() = component.stripped
    public open val coloredText: String get() = component.string

    public class Pre(override val component: Component) : ChatReceivedEvent(), Cancellable {
        // "component" cant change in the Pre event, so we can have text and coloredText not be getters
        override val text: String = component.stripped
        override val coloredText: String = component.string
    }

    public class Post(override var component: Component, var id: String? = null) : ChatReceivedEvent()

    override fun post(bus: EventBus) = bus.post(this, null, onError)

    companion object {
        private val onError: ((Throwable) -> Unit)? = if (McClient.isDev) null else {
            { SkyBlockAPI.logger.error("Error posting ChatReceivedEvent", it) }
        }
    }
}
