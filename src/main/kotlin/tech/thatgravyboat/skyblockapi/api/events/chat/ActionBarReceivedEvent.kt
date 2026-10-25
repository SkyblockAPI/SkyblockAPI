package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public abstract class ActionBarReceivedEvent : SkyBlockEvent() {

    public abstract val component: Component

    public open val text: String = component.stripped
    public open val coloredText: String = component.string

    /**
     * Dispatched first before any other chat event, allows you to cancel the event.
     */
    public class Pre(override val component: Component) : ActionBarReceivedEvent(), Cancellable

    /**
     * Dispatched for all cancelled messages, this also includes messages cancelled by other mods.
     */
    public class Cancelled internal constructor(override val component: Component) : ActionBarReceivedEvent()

    /**
     * Dispatched second as long as the message didn't get cancelled, allows you to modify the message.
     */
    public class Post(override var component: Component) : ActionBarReceivedEvent() {
        // The component can be changed for this instance, so we can't cache the values.
        override val text: String get() = component.stripped
        override val coloredText: String get() = component.string
    }

    /**
     * Dispatched last, this event implies the message will be shown to the player.
     */
    public class Sent internal constructor(override val component: Component) : ActionBarReceivedEvent()
}
