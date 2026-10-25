package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent.Pre
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.UUID

public abstract class ActionBarReceivedEvent : SkyBlockEvent() {

    public abstract val component: Component

    public open val text: String = component.stripped
    public open val coloredText: String = component.string

    /**
     * Dispatched first before any other action bar event, allows you to cancel the event.
     */
    public class Pre(override val component: Component) : ActionBarReceivedEvent(), Cancellable {
        //? < 26.4 {
        override val text: String get() = super.text
        override val coloredText: String get() = super.text
        //? }
    }

    /**
     * Dispatched for all cancelled components, this also includes components cancelled by other mods.
     */
    public class Cancelled internal constructor(override val component: Component) : ActionBarReceivedEvent()

    /**
     * Dispatched second as long as the event didn't get cancelled, allows you to modify the component.
     */
    public class Post(override var component: Component) : ActionBarReceivedEvent() {
        // The component can be changed for this instance, so we can't cache the values.
        override val text: String get() = component.stripped
        override val coloredText: String get() = component.string
    }

    /**
     * Dispatched last, this event implies the event will be shown to the player.
     */
    public class Sent internal constructor(override val component: Component) : ActionBarReceivedEvent()
}
