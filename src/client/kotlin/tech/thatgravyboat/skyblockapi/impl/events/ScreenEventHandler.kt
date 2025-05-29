package tech.thatgravyboat.skyblockapi.impl.events

import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.api.item.getClickAction
import tech.thatgravyboat.skyblockapi.api.item.getVisualItem
import tech.thatgravyboat.skyblockapi.utils.extentions.getHoveredSlot

@Module
object ScreenEventHandler {

    init {
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            ScreenInitializedEvent(screen).post(SkyBlockAPI.eventBus)
        }
    }

    @Subscription
    fun preScreenClick(event: ScreenMouseClickEvent.Pre) {
        if (event.screen !is AbstractContainerScreen<*>) return
        val consumer = event.screen.getHoveredSlot()?.item?.getVisualItem()?.getClickAction() ?: return
        consumer.accept(event.button)
        event.cancel()
    }
}
