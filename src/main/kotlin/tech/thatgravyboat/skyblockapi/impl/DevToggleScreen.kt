package tech.thatgravyboat.skyblockapi.impl

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.DebugEntry
import tech.thatgravyboat.skyblockapi.utils.DebugSelect
import tech.thatgravyboat.skyblockapi.utils.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.underlined

// im sorry to whoever has to read this and understand this
internal class DevToggleScreen : Screen(CommonComponents.EMPTY) {

    val debugEntries: List<DebugEntry> = DevUtils.allDevUtils.flatMap { it.allDebugEntries }

    private fun debugEntryByPath(path: List<String>): DebugEntry? {
        return debugEntries.find { it.location.split() == path }
    }
    private fun Identifier.split(): List<String> = buildList {
        add(namespace)
        addAll(path.split('/', '.', '-'))
    }

    private fun MutableComponent.appendDefaultSelected(text: String) {
        append(" ")
        append("[P]") {
            color = TextColor.DARK_PURPLE
            hover = Text.of("Set $text by default on launch", TextColor.DARK_PURPLE)
        }
    }

    private fun getSelected(column: Int): List<Entry> {
        // if the column is the exact same size as the selected path, it means that this is the last column
        // so, if the path exactly matches the one of a debug select, that means that its currently selected, and so
        // we show its states
        if (column == selectedPath.size) {
            val selectedEntry = debugEntryByPath(selectedPath)
            if (selectedEntry is DebugSelect<*>) {
                val supportsChangingDefault = selectedEntry.canSetDefault()
                return buildList {
                    fun addValue(name: String?) {
                        val canBeDefault = supportsChangingDefault && name != null
                        val isDefault = selectedEntry.getDefault() == name

                        val selected = selectedEntry.stateName() == name
                        fun component(enabled: Int, default: Int) = Text.of {
                            if (selected) {
                                append("[x]", enabled)
                            }
                            else append("[_]")
                            color = default
                            append(" ")
                            append(name ?: "<null>") {
                                if (selected) underlined = true
                            }

                            if (canBeDefault && isDefault) {
                                appendDefaultSelected("as selected")
                            }
                        }
                        entry(
                            message = component(TextColor.DARK_GREEN, TextColor.GRAY),
                            hovered = component(TextColor.GREEN, TextColor.WHITE),
                            leftClick = { selectedEntry.setByName(name) },
                            rightClick = if (!supportsChangingDefault) null else { {
                                if (selected) selectedEntry.setDefault(null)
                                else selectedEntry.setDefault(name)
                            } },
                        ).let(::add)
                    }
                    addValue(null)
                    selectedEntry.states().sorted().forEach(::addValue)

                }
            }
        }

        val isNamespace = column == 0

        return debugEntries.asSequence()
            .map { entry ->
                entry.location.split()
            }.filter { split ->
                split.take(column) == selectedPath.take(column)
            }.mapNotNull { split ->
                if (split.size <= column) return@mapNotNull null
                split.take(column + 1)
            }.distinct()
            .sortedBy { it.last() }
            .map { path ->
                path to debugEntryByPath(path)
            }.sortedBy { (_, entry) ->
                when (entry) {
                    null -> 0
                    is DebugSelect<*> -> 1
                    is DebugToggle -> 2
                }
            }.map { (path, entry) ->
                val name = path.last()
                val isSelected = isSelected(path)

                when (entry) {
                    is DebugToggle -> {
                        val supportsChangingDefault = entry.canSetDefault()
                        fun component(enabled: Int, default: Int) = Text.of {
                            if (entry.get()) append("[x]") else append("[_]")
                            append(" $name")
                            color = if (entry.get()) enabled else default

                            if (entry.hasDescription()) {
                                hover = Text.of(entry.description, TextColor.YELLOW)
                            }

                            if (supportsChangingDefault && entry.getDefault()) {
                                appendDefaultSelected("to enabled")
                            }
                        }
                        entry(
                            message = component(TextColor.DARK_GREEN, TextColor.GRAY),
                            hovered = component(TextColor.GREEN, TextColor.WHITE),
                            leftClick = { entry.toggle() },
                            rightClick = if (!supportsChangingDefault) null else { { entry.toggleDefault() } },
                        )
                    }
                    is DebugSelect<*> -> {
                        val supportsChangingDefault = entry.canSetDefault()
                        val hasDefault = supportsChangingDefault && entry.hasDefault()
                        val hasState = entry.hasState()

                        fun component(enabled: Int, default: Int, selected: Int) = Text.of {
                            this.color = if (hasState) enabled else default
                            if (!isSelected) {
                                append("[☰] $name")
                            } else {
                                append("[", selected)
                                append("☰")
                                append("]", selected)
                                append(" ")
                                append(name) {
                                    underlined = true
                                }
                            }


                            if (entry.hasDescription()) {
                                hover = Text.of(entry.description, TextColor.YELLOW)
                            }

                            if (hasDefault) appendDefaultSelected("to enabled")
                        }

                        entry(
                            message = component(TextColor.DARK_GREEN, TextColor.GRAY, TextColor.YELLOW),
                            hovered = component(TextColor.GREEN, TextColor.WHITE, TextColor.ORANGE),
                            leftClick = { selectedPath = if (!isSelected) path else path.dropLast(1) },
                            rightClick = {
                                if (hasDefault) entry.setDefault(null) else entry.set(null)
                            },
                        )
                    }
                    else -> {
                        fun component(selected : Int, default: Int, selectedNamespace: Int, defaultNamespace: Int) = Text.of(name) {
                            if (isSelected) {
                                color = if (isNamespace) selectedNamespace else selected
                                underlined = true
                            } else {
                                color = if (isNamespace) defaultNamespace else default
                            }
                        }
                        entry(
                            component(0x007788, TextColor.GRAY, TextColor.DARK_GREEN, TextColor.YELLOW),
                            component(TextColor.AQUA, TextColor.WHITE, TextColor.GREEN, TextColor.GOLD),
                            leftClick = { selectedPath = if (!isSelected) path else path.dropLast(1) },
                            rightClick = { selectedPath = path.dropLast(1) },
                        )
                    }
                }
            }.toList()
    }

    private var selectedPath: List<String> = emptyList()

    init {
        // if there's only one namespace, we just automatically select that one when creating the screen
        val firstNamespace = debugEntries.firstOrNull()?.location?.namespace
        if (firstNamespace != null && debugEntries.all { it.location.namespace == firstNamespace }) {
            selectedPath = listOf(firstNamespace)
        }
    }

    private fun isSelected(path: List<String>): Boolean {
        return selectedPath.take(path.size) == path
    }

    override fun init() {
        super.init()
        val horizontalLayout = LinearLayout.horizontal().spacing(5)

        // 10 just in case a mod creates an insanely long debug entry
        for (column in 0..10) {
            val widgets = getSelected(column)
            if (widgets.isEmpty()) break

            val currentLayout = LinearLayout.vertical().spacing(2)
            widgets.forEach(currentLayout::addChild)
            currentLayout.arrangeElements()

            horizontalLayout.addChild(currentLayout)
        }
        horizontalLayout.arrangeElements()

        horizontalLayout.setPosition(10, 10)
        horizontalLayout.visitWidgets(this::addRenderableWidget)
    }

    private fun entry(
        message: Component,
        hovered: Component,
        leftClick: () -> Unit,
        rightClick: (() -> Unit)? = null,
    ) = Entry(message, hovered, rightClick, leftClick)

    private inner class Entry(
        message: Component,
        private val hovered: Component,
        private val rightClick: (() -> Unit)?,
        private val leftClick: () -> Unit
    ) : StringWidget(0, 0, message.width, McFont.height, message, McFont.self) {
        init {
            // needs to be "active" for onClick to actually run
            active = true
        }

        override fun isValidClickButton(buttonInfo: MouseButtonInfo): Boolean {
            return when (buttonInfo.button()) {
                InputConstants.MOUSE_BUTTON_LEFT -> true
                InputConstants.MOUSE_BUTTON_RIGHT -> rightClick != null
                else -> false
            }
        }

        override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, a)
            this.handleCursor(graphics) // we call this so the cursor actually changes when hovering
        }

        override fun getMessage(): Component {
            return if (isHovered) hovered
            else super.getMessage()
        }

        override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
            when (event.button()) {
                InputConstants.MOUSE_BUTTON_LEFT -> this@Entry.leftClick()
                InputConstants.MOUSE_BUTTON_RIGHT if rightClick != null -> this@Entry.rightClick()
                else -> return
            }
            this@DevToggleScreen.rebuildWidgets()
        }
    }

    @Module
    companion object {
        @Subscription
        context(event: RegisterSkyblockApiCommandsEvent)
        private fun registerCommands() {
            event.registerWithCallback("toggle screen") {
                McClient.setScreenAsync { DevToggleScreen() }
            }
        }
    }

}
