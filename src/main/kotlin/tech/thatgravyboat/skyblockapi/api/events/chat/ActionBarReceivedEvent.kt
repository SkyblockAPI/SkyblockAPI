package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public abstract class ActionBarReceivedEvent : SkyBlockEvent() {

    public abstract val component: Component

    public open val text: String get() = component.stripped
    public open val coloredText: String get() = component.string

    /**
     * Dispatched first before any other action bar event, allows you to cancel the event.
     */
    //~ if >= 26.4 'public constructor' -> 'internal constructor'
    public class Pre public constructor(override val component: Component) : ActionBarReceivedEvent(), Cancellable {
        override val text: String = super.text
        override val coloredText: String = super.coloredText
    }

    /**
     * Dispatched for all cancelled components, this also includes components cancelled by other mods.
     */
    public class Cancelled internal constructor(override val component: Component) : ActionBarReceivedEvent() {
        override val text: String = super.text
        override val coloredText: String = super.coloredText
    }

    /**
     * Dispatched second as long as the event didn't get cancelled, allows you to modify the component.
     */
    //~ if >= 26.4 'public constructor' -> 'internal constructor'
    public class Post public constructor(override var component: Component) : ActionBarReceivedEvent()

    /**
     * Dispatched last, this event implies the event will be shown to the player.
     */
    public class Sent internal constructor(override val component: Component) : ActionBarReceivedEvent() {
        override val text: String = super.text
        override val coloredText: String = super.coloredText
    }
}
