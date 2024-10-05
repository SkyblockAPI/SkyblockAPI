package tech.thatgravyboat.skyblockapi.impl.debug

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.extensions.getHoveredSlot
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugInventory {

    private var enabled = false

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("inventory") {
                callback {
                    enabled = !enabled
                    Text.of("[SkyBlockAPI] Debug inventory: $enabled") {
                        this.color = TextColor.YELLOW
                    }.send()
                }
            }
        }
    }

    @Subscription
    fun onForegroundRender(event: RenderScreenForegroundEvent) {
        if (!enabled) return
        val menuScreen = McScreen.asMenu ?: return
        val slot = menuScreen.getHoveredSlot() ?: return

        event.graphics.drawCenteredString(
            McClient.self.font,
            "${slot.index}",
            8,
            8,
            0xFFFFFF
        )
    }

}
