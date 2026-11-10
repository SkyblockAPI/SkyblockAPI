package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public abstract class TitleReceivedEvent : SkyBlockEvent() {

    public abstract val component: Component

    public open val text: String get() = component.stripped
    public open val coloredText: String get() = component.string

    public abstract class Title(override val component: Component) : TitleReceivedEvent() {
        /**
         * Dispatched first before any other title event, allows you to cancel the event.
         */
        public class Pre public constructor(component: Component) : Title(component), Cancellable {
            override val text: String = super.text
            override val coloredText: String = super.coloredText
        }

        /**
         * Dispatched for all cancelled components, this also includes components cancelled by other mods.
         */
        public class Cancelled internal constructor(component: Component) : Title(component) {
            override val text: String = super.text
            override val coloredText: String = super.coloredText
        }

        /**
         * Dispatched second as long as the event didn't get cancelled, allows you to modify the component.
         */
        public class Post internal constructor(override var component: Component) : Title(component)

        /**
         * Dispatched last, this event implies the event will be shown to the player.
         */
        public class Sent internal constructor(component: Component) : Title(component) {
            override val text: String = super.text
            override val coloredText: String = super.coloredText
        }
    }

    public abstract class Subtitle(override val component: Component) : TitleReceivedEvent() {
        /**
         * Dispatched first before any other title event, allows you to cancel the event.
         */
        public class Pre public constructor(component: Component) : Subtitle(component), Cancellable {
            override val text: String = super.text
            override val coloredText: String = super.coloredText
        }

        /**
         * Dispatched for all cancelled components, this also includes components cancelled by other mods.
         */
        public class Cancelled internal constructor(component: Component) : Subtitle(component) {
            override val text: String = super.text
            override val coloredText: String = super.coloredText
        }

        /**
         * Dispatched second as long as the event didn't get cancelled, allows you to modify the component.
         */
        public class Post internal constructor(override var component: Component) : Subtitle(component)

        /**
         * Dispatched last, this event implies the event will be shown to the player.
         */
        public class Sent internal constructor(component: Component) : Subtitle(component) {
            override val text: String = super.text
            override val coloredText: String = super.coloredText
        }
    }
}
