package tech.thatgravyboat.skyblockapi.impl.debug

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudElementEvent
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.extentions.enumSetOf
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Module
object DebugHudElement {

    private val elementsToHide = enumSetOf<HudElement>()

    @Subscription
    internal fun onCommandRegistration(event: RegisterSkyblockApiCommandsEvent) {
        event.register("hudelement") {
            then("element", EnumArgument<HudElement>()) {
                callback {
                    val element = argument<HudElement>("element")
                    if (element in elementsToHide) {
                        Text.sendDebug("Unhiding element $element in hud")
                        elementsToHide.remove(element)
                    } else {
                        Text.sendDebug("Hiding element $element in hud")
                        elementsToHide.add(element)
                    }
                }
            }
        }
    }

    @Subscription
    fun onWidgetShow(event: RenderHudElementEvent) {
        if (event.element in elementsToHide) {
            event.cancel()
        }
    }

}
