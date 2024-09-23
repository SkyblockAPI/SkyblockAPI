package tech.thatgravyboat.skyblockapi.api.events.chat

import net.minecraft.network.chat.Component
import net.minecraft.util.StringUtil
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyblockEvent

class ActionbarReceivedEvent(var component: Component) : CancellableSkyblockEvent() {

    val text: String
        get() = StringUtil.stripColor(component.string)
}
