package tech.thatgravyboat.skyblockapi.impl

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.ScrollableLayout
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.devutils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugEntry
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugMultiSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.underlined
import tech.thatgravyboat.skyblockapi.utils.DevUtils as OldDevUtils

// im sorry to whoever has to read this and understand this
internal class DevToggleScreen : Screen(CommonComponents.EMPTY) {

    val debugEntries: List<DebugEntry> = buildList {
        @Suppress("DEPRECATION")
        addAll(OldDevUtils.allDevUtils.flatMap { it.debugEntries.values.map { it.toNew() } })
        addAll(DevUtils.allDevUtils.flatMap { it.debugEntries.values })
    }

    private fun debugEntryByPath(path: List<String>): DebugEntry? {
        return debugEntries.find { it.splitLocation() == path }
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
            val supportsChangingDefault = selectedEntry?.canSetDefault() ?: false
            when (selectedEntry) {
                is DebugSelect<*> -> {
                    return buildList {
                        fun addValue(name: String?) {
                            val canBeConfigDefault = supportsChangingDefault && (name != selectedEntry.getDefaultStateName())
                            val isConfigDefault = selectedEntry.getDefault() == name

                            val isSelected = selectedEntry.stateName() == name
                            fun component(enabled: Int, default: Int) = Text.of {
                                if (isSelected) {
                                    append("[x]", enabled)
                                } else append("[_]")
                                color = default
                                append(" ")
                                append(name ?: "<null>") {
                                    if (isSelected) underlined = true
                                }

                                if (canBeConfigDefault && isConfigDefault) {
                                    appendDefaultSelected("as selected")
                                }
                            }
                            entry(
                                message = component(TextColor.DARK_GREEN, TextColor.GRAY),
                                hovered = component(TextColor.GREEN, TextColor.WHITE),
                                leftClick = { selectedEntry.setByName(name) },
                                rightClick = if (!supportsChangingDefault) null else Runnable {
                                    if (isConfigDefault) selectedEntry.setDefault(selectedEntry.getDefaultStateName())
                                    else selectedEntry.setDefault(name)
                                },
                            ).let(::add)
                        }

                        val defaultState = selectedEntry.getDefaultStateName()
                        addValue(defaultState)
                        selectedEntry.states().sorted().filterNot { it == defaultState }.forEach(::addValue)
                    }
                }

                is DebugMultiSelect<*> -> {
                    return buildList {
                        fun addValue(name: String) {
                            val isSelected = selectedEntry.isSelectedByName(name)
                            val isConfigDefault = selectedEntry.getIsDefaultSelectedByName(name)

                            fun component(enabled: Int, default: Int) = Text.of {
                                if (isSelected) {
                                    append("[x]", enabled)
                                } else append("[_]")
                                color = default
                                append(" ")
                                append(name) {
                                    if (isSelected) underlined = true
                                }

                                if (supportsChangingDefault && isConfigDefault) {
                                    appendDefaultSelected("as selected")
                                }
                            }
                            entry(
                                message = component(TextColor.DARK_GREEN, TextColor.GRAY),
                                hovered = component(TextColor.GREEN, TextColor.WHITE),
                                leftClick = { selectedEntry.toggleByName(name) },
                                rightClick = if (!supportsChangingDefault) null else Runnable {
                                    selectedEntry.setDefaultSelectedByName(name, !isConfigDefault)
                                },
                            ).let(::add)
                        }
                        selectedEntry.states().sorted().forEach(::addValue)
                    }
                }

                else -> {}
            }
        }

        val isNamespace = column == 0

        return debugEntries.asSequence()
            .map { entry ->
                // splits the entry location into different strings separated by `/.-`, where the index
                // indicates which part should be displayed in each column
                entry.splitLocation()
            }.filter { split ->
                // only shows the paths that actually match the selected path up until the current column
                split.take(column) == selectedPath.take(column)
            }.mapNotNull { split ->
                // if the current path is smaller than the column path, we ignore it, since that means that
                // it was already displayed in a previous column (I think)
                if (split.size <= column) return@mapNotNull null
                // we take 1 extra element from column, as column starts at index 0
                split.take(column + 1)
            }.distinct() // we remove duplicate paths
            .sortedBy { it.last() } // we sort by the name, which is the last split
            .map { path ->
                path to debugEntryByPath(path)
            }.sortedBy { (_, entry) ->
                // debug selects should go first, then multi selects, then toggles, then other branches
                when (entry) {
                    null -> 0
                    is DebugSelect<*> -> 1
                    is DebugMultiSelect<*> -> 2
                    is DebugToggle -> 3
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
                            rightClick = if (!supportsChangingDefault) null else Runnable { entry.toggleDefault() },
                        )
                    }

                    is DebugSelect<*> -> {
                        val supportsChangingConfig = entry.canSetDefault()
                        val hasSetConfig = supportsChangingConfig && entry.hasDefault()
                        val isSetDefaultState = entry.isDefaultState()

                        fun component(enabled: Int, default: Int, selected: Int) = Text.of {
                            this.color = if (isSetDefaultState) default else enabled
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

                            if (hasSetConfig) appendDefaultSelected("to enabled")
                        }

                        entry(
                            message = component(TextColor.DARK_GREEN, TextColor.GRAY, TextColor.YELLOW),
                            hovered = component(TextColor.GREEN, TextColor.WHITE, TextColor.ORANGE),
                            leftClick = { selectedPath = if (!isSelected) path else path.dropLast(1) },
                            rightClick = {
                                if (hasSetConfig) entry.setDefault(entry.getDefaultStateName())
                                else entry.set(entry.getDefaultState())
                            },
                        )
                    }

                    is DebugMultiSelect<*> -> {
                        val supportsChangingConfig = entry.canSetDefault()
                        val hasSetConfig = supportsChangingConfig && entry.hasDefault()
                        val isSetDefaultState = entry.isDefaultState()

                        fun component(enabled: Int, default: Int, selected: Int) = Text.of {
                            this.color = if (isSetDefaultState) default else enabled
                            if (!isSelected) {
                                append("[+] $name")
                            } else {
                                append("[", selected)
                                append("+")
                                append("]", selected)
                                append(" ")
                                append(name) {
                                    underlined = true
                                }
                            }

                            if (entry.hasDescription()) {
                                hover = Text.of(entry.description, TextColor.YELLOW)
                            }

                            if (hasSetConfig) {
                                appendDefaultSelected("to custom")
                            }
                        }

                        entry(
                            message = component(TextColor.DARK_GREEN, TextColor.GRAY, TextColor.YELLOW),
                            hovered = component(TextColor.GREEN, TextColor.WHITE, TextColor.ORANGE),
                            leftClick = { selectedPath = if (!isSelected) path else path.dropLast(1) },
                            rightClick = {
                                if (hasSetConfig) entry.setDefault(entry.getDefaultStateSerialized())
                                else entry.setBySerialized(entry.getDefaultStateSerialized())
                            },
                        )
                    }

                    else -> {
                        fun component(selected: Int, default: Int, selectedNamespace: Int, defaultNamespace: Int) = Text.of(name) {
                            if (isSelected) {
                                color = if (isNamespace) selectedNamespace else selected
                                underlined = true
                            } else {
                                color = if (isNamespace) defaultNamespace else default
                            }
                        }
                        entry(
                            // slightly darker aqua
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

    // scrollables of the current screen, to be used for restoring the scroll amount when refreshing
    private val scrollables = mutableMapOf<List<String>, ScrollableLayout>()

    override fun init() {
        super.init()
        val horizontalLayout = LinearLayout.horizontal().spacing(5)
        val maxHeight = height - PADDING * 2

        // scroll amounts of the previous screen
        val scrollAmounts = scrollables.mapValues { (_, scrollable) -> scrollable.container.scrollAmount() }
        scrollables.clear()

        // 10 just in case a mod creates an insanely long debug entry
        for (column in 0..10) {
            val currentPath = selectedPath.take(column)
            val widgets = getSelected(column)
            if (widgets.isEmpty()) break

            val currentLayout = LinearLayout.vertical().spacing(2)
            widgets.forEach(currentLayout::addChild)
            currentLayout.arrangeElements()

            val scrollable = ScrollableLayout(minecraft, currentLayout, maxHeight)
            scrollables[currentPath] = scrollable

            horizontalLayout.addChild(scrollable)
        }
        horizontalLayout.arrangeElements()

        horizontalLayout.setPosition(PADDING, PADDING)

        // update the scroll amount where applicable
        scrollables.forEach { (key, scrollable) ->
            scrollAmounts[key]?.let(scrollable.container::setScrollAmount)
        }

        horizontalLayout.visitWidgets(this::addRenderableWidget)
    }

    private fun entry(
        message: Component,
        hovered: Component,
        leftClick: Runnable,
        rightClick: Runnable? = null,
    ) = Entry(message, hovered, rightClick, leftClick)

    private inner class Entry(
        message: Component,
        private val hovered: Component,
        private val rightClick: Runnable?,
        private val leftClick: Runnable,
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
                InputConstants.MOUSE_BUTTON_LEFT -> this@Entry.leftClick.run()
                InputConstants.MOUSE_BUTTON_RIGHT if rightClick != null -> this@Entry.rightClick.run()
                else -> return
            }
            this@DevToggleScreen.rebuildWidgets()
        }
    }

    @Module
    companion object {
        private const val PADDING = 10

        @Subscription
        context(event: RegisterSkyblockApiCommandsEvent)
        private fun registerCommands() {
            event.registerWithCallback("toggle screen") {
                McClient.setScreenAsync { DevToggleScreen() }
            }
        }
    }

}
