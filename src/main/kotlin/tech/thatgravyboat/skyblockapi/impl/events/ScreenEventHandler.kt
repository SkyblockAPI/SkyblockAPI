package tech.thatgravyboat.skyblockapi.impl.events

import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.SlotClickEvent
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
        val screen = event.screen as? AbstractContainerScreen<*> ?: return
        val slot = screen.getHoveredSlot() ?: return
        if (SlotClickEvent(slot.item, slot, event.button, screen).post()) event.cancel()
    }

    @Subscription(priority = Subscription.HIGHEST)
    fun onSlotClick(event: SlotClickEvent) {
        val consumer = event.item.getVisualItem()?.getClickAction() ?: return
        if (consumer.accept(event.button) != null) event.cancel()
    }
}
