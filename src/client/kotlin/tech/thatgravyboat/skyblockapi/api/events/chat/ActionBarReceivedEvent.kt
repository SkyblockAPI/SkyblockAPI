package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

class ActionBarReceivedEvent(var component: Component) : CancellableSkyBlockEvent() {

    val text: String
        get() = component.stripped

    var coloredText: String
        get() = component.string
        set(value) = run { component = Text.of(value) }
}
