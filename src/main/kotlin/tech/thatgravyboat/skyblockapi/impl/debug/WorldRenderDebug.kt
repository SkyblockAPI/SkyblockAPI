package tech.thatgravyboat.skyblockapi.impl.debug

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.debugToggle

@Module
object WorldRenderDebug {

    private val isEnabled by debugToggle("render_debugs")

    @Subscription
    context(event: RenderWorldEvent)
    private fun onWorldRender() {
        event.atCamera {
            translate(0f, 100f, 0f)
            event.drawString("test", 0f, 0f, (-1).toUInt())
        }
    }

}
