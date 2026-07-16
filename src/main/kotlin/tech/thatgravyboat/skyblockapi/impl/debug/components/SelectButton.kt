package tech.thatgravyboat.skyblockapi.impl.debug.components

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.platform.cursor.CursorTypes
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.InputWithModifiers
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.CommonColors
import net.minecraft.util.Mth
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.drawFilledBox
import tech.thatgravyboat.skyblockapi.platform.drawOutline
import tech.thatgravyboat.skyblockapi.platform.drawSprite
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.underlined
import kotlin.math.max
import kotlin.math.min

private const val SELECT_BUTTON_SPACING = 2
private const val SELECT_BUTTON_PADDING = 3
private const val SELECT_BUTTON_HEIGHT = 10
private const val SELECT_BUTTON_MAX_ENTRIES = 10
private const val SELECT_BUTTON_MAX_HEIGHT =
    SELECT_BUTTON_MAX_ENTRIES * SELECT_BUTTON_HEIGHT + (SELECT_BUTTON_MAX_ENTRIES - 1) * SELECT_BUTTON_SPACING + SELECT_BUTTON_PADDING * 2

private val SPRITES = WidgetSprites(
    Identifier.withDefaultNamespace("widget/button"),
    Identifier.withDefaultNamespace("widget/button_disabled"),
    Identifier.withDefaultNamespace("widget/button_highlighted"),
)

internal class SelectButton<T>(width: Int, height: Int) : AbstractButton(0, 0, width, height, CommonComponents.EMPTY) {

    private val entries = mutableListOf<Entry<T>>()
    var singleValue: Boolean = false
    var onChange: (List<T>) -> Unit = {}

    private fun onPress(value: T): Boolean {
        val entry = this.entries.find { it.value == value } ?: return false
        if (this.singleValue && !entry.selected) {
            this.entries.forEach { it.selected = it.value == value }
        } else if (!this.singleValue) {
            entry.selected = !entry.selected
        } else {
            return false
        }

        this.onChange.invoke(this.entries.filter(Entry<T>::selected).map(Entry<T>::value))
        return true
    }

    fun withEntry(value: T, selectedText: Component, unselectedText: Component, selected: Boolean = false): SelectButton<T> {
        this.entries.add(Entry(value, selectedText, unselectedText, selected))
        return this
    }

    override fun onPress(input: InputWithModifiers) {
        McClient.setScreen(SelectOverlay(this))
    }

    override fun getMessage(): Component {
        val selected = this.entries.filter(Entry<T>::selected)

        return when {
            this.singleValue -> selected.first().unselectedText
            else -> Component.literal("${selected.size} Selected")
        }
    }

    override fun updateWidgetNarration(output: NarrationElementOutput?) {}

    //~ if >= 26.1 'renderContents' -> 'extractContents'
    override fun extractContents(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val text = this.getMessage()

        graphics.drawSprite(SPRITES.get(this.active, this.isHoveredOrFocused), this.x, this.y, this.width, this.height)
        graphics.drawString(text, this.x + (this.width - text.width) / 2, this.y + (this.height - 8) / 2, shadow = true)
    }

    private data class Entry<T>(
        val value: T,
        val selectedText: Component,
        val unselectedText: Component,
        var selected: Boolean,
    )

    private class SelectOverlay<T>(private val button: SelectButton<T>) : Overlay() {

        private val x get() = button.x
        private val y get() = button.y + button.height

        private var offset = 0
            set(value) {
                field = Mth.clamp(value, 0, max(0, button.entries.size - SELECT_BUTTON_MAX_ENTRIES))
            }

        private fun isHovered(mouseX: Number, mouseY: Number): Boolean {
            return mouseX.toInt() >= this.x && mouseX.toInt() <= this.x + button.width && mouseY.toInt() >= this.y && mouseY.toInt() <= this.y + SELECT_BUTTON_MAX_HEIGHT
        }

        //~ if >= 26.1 'renderBackground' -> 'extractBackground'
        override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
            //~ if >= 26.1 'renderBackground' -> 'extractBackground'
            super.extractBackground(graphics, mouseX, mouseY, partialTick)

            val height = min(
                this.button.entries.size * SELECT_BUTTON_HEIGHT + (this.button.entries.size - 1) * SELECT_BUTTON_SPACING + SELECT_BUTTON_PADDING * 2,
                SELECT_BUTTON_MAX_HEIGHT,
            )
            graphics.drawFilledBox(x, y, button.width, height, -1072689136)
            graphics.drawOutline(x, y, button.width, height, CommonColors.WHITE)

            if (this.button.entries.size > SELECT_BUTTON_MAX_ENTRIES) {
                val y = this.y + SELECT_BUTTON_PADDING
                val x = this.x + button.width - 5

                val extraEntries = this.button.entries.size - SELECT_BUTTON_MAX_ENTRIES + 1
                val scrollBarHeight = height - SELECT_BUTTON_PADDING * 2
                val scrollBarThumbHeight = if (extraEntries > 0) Math.ceilDiv(scrollBarHeight, extraEntries) else 0
                val scrollBarThumbY = if (extraEntries > 0) min(y + offset * scrollBarThumbHeight, y + scrollBarHeight - scrollBarThumbHeight) else y

                graphics.drawFilledBox(x, y, 2, scrollBarHeight, CommonColors.DARK_GRAY)
                graphics.drawFilledBox(x, scrollBarThumbY, 2, scrollBarThumbHeight, CommonColors.WHITE)
            }
        }

        //~ if >= 26.1 'render(' -> 'extractRenderState('
        override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
            graphics.scissor(x, y, button.width, SELECT_BUTTON_MAX_HEIGHT) {
                for (i in 0 until SELECT_BUTTON_MAX_ENTRIES) {
                    val index = i + offset
                    if (index >= button.entries.size) break

                    val entry = button.entries[index]
                    val entryY = y + SELECT_BUTTON_PADDING + i * SELECT_BUTTON_HEIGHT + i * SELECT_BUTTON_SPACING
                    val hovered = mouseX >= x && mouseX <= x + button.width && mouseY >= entryY && mouseY <= entryY + SELECT_BUTTON_HEIGHT

                    val text = Text.join(if (entry.selected) entry.selectedText else entry.unselectedText) {
                        this.underlined = hovered
                    }

                    graphics.drawString(text, x + SELECT_BUTTON_PADDING, entryY + 1, shadow = true)

                    if (hovered) {
                        graphics.requestCursor(CursorTypes.POINTING_HAND)
                    }
                }
            }
        }

        override fun mouseClicked(event: MouseButtonEvent, isDoubleClick: Boolean): Boolean {
            if (!this.isHovered(event.x, event.y)) {
                this.onClose()
            } else {
                val index = ((event.y - this.y - SELECT_BUTTON_PADDING) / (SELECT_BUTTON_HEIGHT + SELECT_BUTTON_SPACING)).toInt() + offset
                if (index in this.button.entries.indices) {
                    val entry = this.button.entries[index]
                    if (this.button.onPress(entry.value) && this.button.singleValue) {
                        this.onClose()
                    }
                }
            }
            return true
        }

        override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
            if (!this.isHovered(mouseX, mouseY)) return false
            this.offset -= scrollY.toInt()
            return true
        }

        override fun keyPressed(event: KeyEvent): Boolean {
            when (event.key) {
                InputConstants.KEY_HOME -> this.offset = 0
                InputConstants.KEY_END -> this.offset = button.entries.size - SELECT_BUTTON_MAX_ENTRIES
                InputConstants.KEY_PAGEUP -> this.offset -= SELECT_BUTTON_MAX_ENTRIES
                InputConstants.KEY_PAGEDOWN -> this.offset += SELECT_BUTTON_MAX_ENTRIES
                InputConstants.KEY_UP -> this.offset -= 1
                InputConstants.KEY_DOWN -> this.offset += 1
                else -> return false
            }
            return true
        }
    }
}
