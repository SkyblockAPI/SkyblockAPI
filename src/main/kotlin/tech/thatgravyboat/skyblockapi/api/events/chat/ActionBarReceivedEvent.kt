package tech.thatgravyboat.skyblockapi.api.events.chat

import me.owdding.dfu.item.LegacyTextFixer
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

abstract class ActionBarReceivedEvent : SkyBlockEvent() {

    abstract val component: Component

    val text: String get() = component.stripped
    val coloredText: String get() = component.string

    class Pre(override val component: Component): ActionBarReceivedEvent(), Cancellable
    class Post(override var component: Component): ActionBarReceivedEvent()

    // TODO: remove once hypixel starts sending actual components in action bars
    companion object {
        internal fun preparePre(component: Component): Pre {
            val fixedComponent = if (component.siblings.isEmpty()) LegacyTextFixer.parse(component.string) else component
            return Pre(fixedComponent)
        }

        internal fun preparePost(component: Component): Post {
            val fixedComponent = if (component.siblings.isEmpty()) LegacyTextFixer.parse(component.string) else component
            return Post(fixedComponent)
        }
    }
}
