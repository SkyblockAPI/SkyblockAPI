package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public abstract class ActionBarReceivedEvent : SkyBlockEvent() {

    public abstract val component: Component

    public open val text: String get() = component.stripped
    public open val coloredText: String get() = component.string

    public class Pre(override val component: Component) : ActionBarReceivedEvent(), Cancellable {
        // "component" cant change in the Pre event, so we can have text and coloredText not be getters
        override val text: String = component.stripped
        override val coloredText: String = component.string
    }
    public class Post(override var component: Component) : ActionBarReceivedEvent()
}
