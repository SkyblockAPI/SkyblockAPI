package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudElementEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugHudElement {

    private val elementsToHide = mutableSetOf<HudElement>()

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi hudelement") {
            then("element", StringArgumentType.greedyString(), HudElement.entries.map { it.name }) {
                callback {
                    val element = HudElement.valueOf(StringArgumentType.getString(this, "element"))
                    if (element in elementsToHide) {
                        Text.of("[SkyBlockAPI] Unhiding element $element in hud") {
                            this.color = TextColor.YELLOW
                        }.send()
                        elementsToHide.remove(element)
                    } else {
                        Text.of("[SkyBlockAPI] Hiding element $element in hud") {
                            this.color = TextColor.YELLOW
                        }.send()
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
