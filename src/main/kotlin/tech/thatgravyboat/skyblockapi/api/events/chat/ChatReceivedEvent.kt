package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent.Pre
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.UUID

public abstract class ChatReceivedEvent protected constructor(public val messageId: UUID) : SkyBlockEvent() {
    //? < 26.4
    @Deprecated("No", level = DeprecationLevel.HIDDEN) public constructor(): this(UUID.randomUUID())
    public abstract val component: Component

    public open val text: String get() = component.stripped
    public open val coloredText: String get() = component.string

    /**
     * Dispatched first before any other chat event, allows you to cancel the event.
     */
    public class Pre internal constructor(override val component: Component, messageId: UUID) : ChatReceivedEvent(messageId), Cancellable {
        //? < 26.4
        @Deprecated("No", level = DeprecationLevel.HIDDEN) public constructor(component: Component): this(component, UUID.randomUUID())

        // "component" cant change in the Pre event, so we can have text and coloredText not be getters
        override val text: String = component.stripped
        override val coloredText: String = component.string
    }

    /**
     * Dispatched for all cancelled messages, this also includes messages cancelled by other mods.
     */
    public class Cancelled internal constructor(override var component: Component, messageId: UUID) : ChatReceivedEvent(messageId)

    /**
     * Dispatched second as long as the message didn't get cancelled, allows you to modify the message.
     */
    public class Post internal constructor(override var component: Component, public var id: String? = null, messageId: UUID) : ChatReceivedEvent(messageId) {
        //? < 26.4
        @Deprecated("No", level = DeprecationLevel.HIDDEN) public constructor(component: Component, id: String? = null): this(component, id, UUID.randomUUID())
    }

    /**
     * Dispatched last, this event implies the message will be shown to the player.
     */
    public class Sent internal constructor(override var component: Component, messageId: UUID) : ChatReceivedEvent(messageId)

    override fun post(bus: EventBus): Boolean = bus.post(this, null, onError)

    public companion object {
        private val onError: ((Throwable) -> Unit)? = if (McClient.isDev) null else {
            { SkyBlockAPI.logger.error("Error posting ChatReceivedEvent", it) }
        }
    }
}
