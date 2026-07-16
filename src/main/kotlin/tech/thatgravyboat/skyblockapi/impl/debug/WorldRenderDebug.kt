package tech.thatgravyboat.skyblockapi.impl.debug

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.debugToggle

@Module
object WorldRenderDebug {

    private val isEnabledEntities by debugToggle("render_debugs_entities")
    private val isEnabledTranslucent by debugToggle("render_debugs_translucent")

    @Subscription
    context(event: RenderWorldEvent.AfterEntities)
    private fun onWorldRenderEntities() {
        if (isEnabledEntities) event.atCamera {
            translate(0f, 100f, 0f)
            event.drawString("test entities", 0f, 0f, (-1).toUInt())
        }
    }

    @Subscription
    context(event: RenderWorldEvent.AfterTranslucent)
    private fun onWorldRenderTranslucent() {
        if (isEnabledTranslucent) event.atCamera {
            translate(0f, 120f, 0f)
            event.drawString("test translucent", 0f, 0f, (-1).toUInt())
        }
    }

}
