package tech.thatgravyboat.skyblockapi.api.events.screen

import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public class ScreenInitializedEvent(val screen: Screen) : SkyBlockEvent() {

    public val widgets: MutableList<AbstractWidget>
        get() = Screens.getWidgets(this.screen)
}
