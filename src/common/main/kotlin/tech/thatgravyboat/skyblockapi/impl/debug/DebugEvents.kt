package tech.thatgravyboat.skyblockapi.impl.debug

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.DevModule
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.HypixelJoinEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import java.lang.reflect.Method

@DevModule
object DebugEvents {

    val methodsToWarn = mutableListOf<Method>()

    @Subscription(HypixelJoinEvent::class)
    fun onHypixelJoin() {
        if (methodsToWarn.isEmpty()) return
        SkyBlockAPI.eventBus.unregister(this)
        val text = methodsToWarn.groupBy { it.declaringClass.name }.entries.joinToString { (clazz, methods) ->
            buildString {
                appendLine("$clazz:")
                for (method in methods) {
                    appendLine("    ${method.name}")
                }
            }
        }
        methodsToWarn.clear()
        Text.sendDebug("Found Public Extension Functions! Click to copy") {
            this.color = TextColor.RED
            this.hover = Text.of(text, TextColor.YELLOW)
            onClick {
                McClient.clipboard = text
                Text.sendDebug("Copied Public Extension Functions to clipboard!")
            }
        }
    }

}
