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
import kotlin.reflect.KVisibility.INTERNAL
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.KVisibility.PROTECTED
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.jvm.kotlinFunction

@DevModule
internal object DebugEvents {

    val methodsToWarn = mutableListOf<Method>()
    var hasWarned: Boolean = false
        private set

    fun tryWarn(method: Method) {
        if (!McClient.isDev) return
        if (hasWarned) return
        val ktFunction = method.kotlinFunction ?: return
        val isExtension = ktFunction.extensionReceiverParameter != null
        val visibility = ktFunction.visibility

        when (visibility) {
            PRIVATE, PROTECTED -> return
            INTERNAL -> {
                if (!isExtension) return
                SkyBlockAPI.logger.warn("""
                
                Extension functions for events should be private, as other visibilities will populate the auto complete for the subscribed events.
                
                The method ${method.name} in class ${method.declaringClass.name} is public and has an extension receiver.
                """.trimIndent())
            }
            else -> {
                SkyBlockAPI.logger.warn("""
                
                Functions for events should be private, as they should not be accessible from outside the class.
                
                The method ${method.name} in class ${method.declaringClass.name} is public.
                """.trimIndent())
            }
        }
        methodsToWarn.add(method)
    }

    @Subscription(HypixelJoinEvent::class)
    private fun onHypixelJoin() {
        if (methodsToWarn.isEmpty()) return
        hasWarned = true
        SkyBlockAPI.eventBus.unregister(this)
        val size = methodsToWarn.size
        val text = methodsToWarn.groupBy { it.declaringClass.name }.entries.joinToString("\n") { (clazz, methods) ->
            buildString {
                appendLine("$clazz:")
                for (method in methods) {
                    appendLine("    ${method.name}")
                }
            }
        }
        methodsToWarn.clear()
        Text.sendDebug("Found Public Event Functions ($size)! Click to copy") {
            this.color = TextColor.RED
            this.hover = Text.of(text, TextColor.YELLOW)
            onClick {
                McClient.clipboard = text
                Text.sendDebug("Copied Public Event Functions to clipboard!")
            }
        }
    }

}
