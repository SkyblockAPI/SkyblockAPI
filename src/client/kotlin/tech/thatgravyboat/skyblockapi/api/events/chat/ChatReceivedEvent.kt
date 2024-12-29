package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

abstract class ChatReceivedEvent : SkyBlockEvent() {

    abstract val component: Component

    val text: String get() = component.stripped
    val coloredText: String get() = component.string

    class Pre(override val component: Component): ChatReceivedEvent(), Cancellable
    class Post(override var component: Component, var id: String? = null): ChatReceivedEvent()
}
