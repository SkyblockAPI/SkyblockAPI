package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.ClickEvent

class RunnableClickEvent(val runnable: () -> Unit) : ClickEvent {

    override fun action(): ClickEvent.Action = ClickEvent.Action.OPEN_FILE
}
