package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyblockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

class ChatReceivedEvent(var component: Component, var id: String? = null) : CancellableSkyblockEvent() {

    val text: String
        get() = component.stripped

    val coloredText: String
        get() = component.string
}
