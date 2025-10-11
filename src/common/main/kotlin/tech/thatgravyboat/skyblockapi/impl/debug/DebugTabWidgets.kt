package tech.thatgravyboat.skyblockapi.impl.debug

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.extentions.enumSetOf
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick

@Module
internal object DebugTabWidgets {

    private var logWidgets: Boolean = false
    private val loggedWidgets = enumSetOf<TabWidget>()

    @Subscription
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        if (!logWidgets) return
        if (event.widget !in loggedWidgets) return
        Text.sendDebug("Tab widget changed: ") {
            append(event.widget.name, TextColor.GOLD)
            this.hover = Text.multiline(event.newComponents)
            onClick {
                McClient.clipboard = event.new.joinToString("\n")
                Text.sendDebug("Copied tab widget contents to clipboard.")
            }
        }
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            thenCallback("copy widget widget", EnumArgument<TabWidget>()) {
                val widget = argument<TabWidget>("widget")!!
                if (!widget.isActive) Text.sendDebug("Tab widget ${widget.name} not present.")
                else {
                    val lines = widget.currentLines
                    Text.sendDebug("Contents of tab widget") {
                        append(widget.name, TextColor.GOLD)
                        this.hover = Text.multiline(lines)
                        onClick {
                            McClient.clipboard = lines.joinToString("\n")
                            Text.sendDebug("Copied contents to clipboard.")
                        }
                    }
                }
            }
            then("logtabwidgets") {
                callback {
                    logWidgets = !logWidgets
                    Text.sendDebug("Tab widget logging is now") {
                        if (logWidgets) append(" Enabled", TextColor.GREEN)
                        else append(" Disabled", TextColor.RED)
                    }
                }
                thenCallback("list") {
                    if (loggedWidgets.isEmpty()) {
                        Text.sendDebug("No tab widgets are being logged.")
                    } else {
                        Text.sendDebug("Logged tab widgets: ${loggedWidgets.joinToString { it.name }}")
                    }
                }
                thenCallback("add widget", EnumArgument<TabWidget>()) {
                    val widget = argument<TabWidget>("widget")!!
                    if (loggedWidgets.add(widget)) {
                        Text.sendDebug("Added ${widget.name} to logged tab widgets.")
                    } else {
                        Text.sendDebug("${widget.name} is already being logged.")
                    }
                }
                thenCallback("remove widget", EnumArgument<TabWidget>()) {
                    val widget = argument<TabWidget>("widget")!!
                    if (loggedWidgets.remove(widget)) {
                        Text.sendDebug("Removed ${widget.name} from logged tab widgets.")
                    } else {
                        Text.sendDebug("${widget.name} is not being logged.")
                    }
                }
                thenCallback("clear") {
                    loggedWidgets.clear()
                    Text.sendDebug("Cleared logged tab widgets.")
                }
            }
        }
    }

}
