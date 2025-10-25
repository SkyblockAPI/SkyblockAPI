package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.ClickEvent
import tech.thatgravyboat.skyblockapi.RemoveNextVersion

/**
 * This will crash if used and then the component is serialized,
 * therefore TextStyle.onClick should be used instead.
 */
@RemoveNextVersion(level = DeprecationLevel.ERROR)
class RunnableClickEvent(val runnable: () -> Unit) : ClickEvent {

    override fun action(): ClickEvent.Action = ClickEvent.Action.OPEN_FILE
}
