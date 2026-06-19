package tech.thatgravyboat.skyblockapi.impl.debug.components

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen

internal open class Overlay : Screen(Component.empty()) {

    val background = McScreen.self

    override fun isPauseScreen(): Boolean = this.background?.isPauseScreen ?: false

    override fun added() {
        super.added()
        this.background?.clearFocus()
    }

    override fun repositionElements() {
        this.background?.resize(this.width, this.height)
        super.repositionElements()
    }

    //~ if >= 26.1 'renderBackground' -> 'extractBackground'
    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        //~ if >= 26.1 'renderWithTooltipAndSubtitles' -> 'extractRenderStateWithTooltipAndSubtitles'
        this.background?.extractRenderStateWithTooltipAndSubtitles(graphics, -1, -1, partialTick)
        graphics.nextStratum()
    }

    override fun onClose() {
        McClient.setScreen(this.background)
    }
}
