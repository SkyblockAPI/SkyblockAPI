package tech.thatgravyboat.skyblockapi.impl.debug

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidget
import tech.thatgravyboat.skyblockapi.api.events.info.RenderActionBarWidgetEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.extentions.enumSetOf
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Module
object DebugActionBar {

    private val widgetsToHide = enumSetOf<ActionBarWidget>()

    @Subscription
    internal fun onCommandRegistration(event: RegisterSkyblockApiCommandsEvent) {
        event.register("actionbar") {
            thenCallback("hide widget", EnumArgument<ActionBarWidget>()) {
                val widget = argument<ActionBarWidget>("widget")
                if (widget in widgetsToHide) {
                    Text.sendDebug("Unhiding widget $widget in action bar")
                    widgetsToHide.remove(widget)
                } else {
                    Text.sendDebug("Hiding widget $widget in action bar")
                    widgetsToHide.add(widget)
                }
            }
        }
    }

    @Subscription
    fun onWidgetShow(event: RenderActionBarWidgetEvent) {
        if (event.widget in widgetsToHide) {
            event.cancel()
        }
    }

}
