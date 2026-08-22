package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.common.collect.Multimap
import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemDebugTooltipEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewerCategory
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.function.BiFunction
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Module
object DebugItems {

    val isEnabled: Boolean by debugToggle("item_debug_entries")

    var selectedIndex = 0
    var entriesSize = 0
    var lastItem: ItemStack? = null
    var lastSet: Instant = Instant.DISTANT_PAST
    val toggledEntries = mutableSetOf<ItemDebugCategory>()

    fun updateItem(new: ItemStack?) {
        this.lastItem = new
        this.selectedIndex = 0
        this.entriesSize = new?.getEntries()?.asMap()?.keys?.size ?: 0
        this.toggledEntries.clear()
    }

    @Subscription
    fun onKeyPressed(event: ScreenKeyPressedEvent.Pre) {
        if (!isEnabled) return
        if (!McScreen.isShiftDown) return
        val entries = lastItem?.getEntries() ?: return

        when (event.key) {
            InputConstants.KEY_DOWN -> selectedIndex = (selectedIndex + 1) % entriesSize
            InputConstants.KEY_UP -> selectedIndex =  if (selectedIndex == 0) entriesSize - 1 else selectedIndex - 1
            InputConstants.KEY_RIGHT -> toggledEntries.add(entries.keySet().toList()[this.selectedIndex])
            InputConstants.KEY_LEFT -> toggledEntries.remove(entries.keySet().toList()[this.selectedIndex])
            InputConstants.KEY_C -> McClient.clipboard = buildString {
                append("```")
                if (McScreen.isControlDown) {
                    entries.asMap().forEach { (category, components) ->
                        append("## ").appendLine(category)
                        appendLine(components.joinToString("\n"))
                    }
                } else {
                    val category = entries.keySet().toList()[selectedIndex]
                    append("## ").appendLine(category)
                    appendLine(entries.get(category).joinToString("\n"))
                }
                append("```")
            }
        }
    }

    @Subscription
    fun onForegroundRender(event: RenderScreenForegroundEvent) {
        if (!isEnabled) return
        if (!McScreen.isShiftDown) return
        lastItem ?: return

        buildList {
            add("Up - Go up")
            add("Down - Go down")
            add("Right - Expand category")
            add("Left - Collapse category")
            add("C - Copy selected category")
            add("Ctrl + C - Copy all categories")
        }.forEachIndexed { index, line ->
            event.graphics.drawString(
                line,
                8,
                8 + index * McFont.height,
                0xFFFFFFFF.toInt(),
            )
        }
    }

    @Subscription
    context(event: TickEvent)
    fun tick() {
        if (this.lastItem != null && this.lastSet.since() > 5.seconds) {
            updateItem(null)
        }
    }

    @Subscription
    fun onGetDebugTooltip(event: ItemDebugTooltipEvent) {
        val item = event.item
        val entries = item.getEntries()
        if (lastItem != item || entriesSize != entries?.asMap()?.keys?.size) {
            updateItem(item)
        }
        if (entries == null) return
        this.lastSet = currentInstant()


        if (!McScreen.isShiftDown) {
            event.add(
                Text.of("$entriesSize Debug Entry(s) [Shift]") {
                    this.color = TextColor.DARK_GRAY
                },
            )
            updateItem(item)
        } else {
            event.add(
                Text.join(
                    Text.of("$entriesSize Debug Entry(s) [") { this.color = TextColor.DARK_GRAY },
                    Text.of("Shift") {
                        this.bold = true
                        this.color = TextColor.GRAY
                    },
                    Text.of("]") { this.color = TextColor.DARK_GRAY },
                ),
            )
            entries.asMap().entries.forEachIndexed { index, (category, entries) ->
                val isExpanded = toggledEntries.contains(category)
                val isSelected = index == selectedIndex
                event.add(Text.of {
                    if (isExpanded) {
                        append("v ")
                    } else {
                        append("> ")
                    }
                    append(category.toString())
                    if (isSelected) {
                        this.color = TextColor.GREEN
                    } else {
                        this.color = TextColor.GRAY
                    }
                })
                if (!isExpanded) return@forEachIndexed
                for (component in entries) {
                    event.add(Text.of {
                        this.color = TextColor.GRAY
                        append("  ")
                        append(component)
                    })
                }
            }
        }

    }

}

fun ItemDebugAccessor.getEntries(): Multimap<ItemDebugCategory, Component>? = this.`skyblockapi$getEntries`()
fun ItemStack.getEntries(): Multimap<ItemDebugCategory, Component>? = this.`skyblockapi$getEntries`()

@OptIn(ExperimentalContracts::class)
inline fun ItemDebugAttachable.addDebug(category: ItemDebugCategory, entry: () -> Component) {
    contract {
        callsInPlace(entry, InvocationKind.AT_MOST_ONCE)
    }
    if (!DebugItems.isEnabled) return
    this.`skyblockapi$addEntry`(category, entry())
}

@OptIn(ExperimentalContracts::class)
@JvmName("categoryAddDebug")
inline context(category: ItemDebugCategory) fun ItemDebugAttachable.addDebug(entry: () -> Component) {
    contract {
        callsInPlace(entry, InvocationKind.AT_MOST_ONCE)
    }
    addDebug(category, entry)
}

@OptIn(ExperimentalContracts::class)
@JvmName("categoryAddDebugString")
inline context(category: ItemDebugCategory) fun ItemDebugAttachable.addDebugString(entry: () -> String) {
    contract {
        callsInPlace(entry, InvocationKind.AT_MOST_ONCE)
    }
    addStringDebug(category, entry)
}

@OptIn(ExperimentalContracts::class)
inline fun ItemDebugAttachable.addStringDebug(category: ItemDebugCategory, entry: () -> String) {
    contract {
        callsInPlace(entry, InvocationKind.AT_MOST_ONCE)
    }
    addDebug(category) { Component.literal(entry()) }
}


@JvmName("addDebug")
@Deprecated(message = "Use interface method instead!")
fun ItemStack.addDebug0(category: ItemDebugCategory, entry: () -> Component) {
    if (!DebugItems.isEnabled) return
    this.`skyblockapi$addEntry`(category, entry())
}

@JvmName("addStringDebug")
@Deprecated(message = "Use interface method instead!")
fun ItemStack.addStringDebug0(category: ItemDebugCategory, entry: () -> String) = addDebug(category) { Component.literal(entry()) }

@JvmName("categoryAddDebug")
@Deprecated(message = "Use interface method instead!")
context(category: ItemDebugCategory) fun ItemStack.addDebug0(entry: () -> Component) = addDebug(category, entry)

@Deprecated(message = "Use interface method instead!")
@JvmName("categoryAddDebugString")
context(category: ItemDebugCategory) fun ItemStack.addDebugString0(entry: () -> String) = addStringDebug(category, entry)


fun interface ItemDebugAttachable {
    @Suppress("FunctionName")
    fun `skyblockapi$addEntry`(category: ItemDebugCategory, entry: Component)
}

fun interface ItemDebugCategory : ComponentViewerCategory {
    companion object {
        val fork: BiFunction<ItemDebugCategory, String, ItemDebugCategory> = Util.memoize { parent, name ->
            return@memoize ItemDebugCategory { "$parent/$name" }
        }
    }

    fun fork(name: String) = fork.apply(this, name)
}

interface ItemDebugAccessor : ItemDebugAttachable {
    override fun `skyblockapi$addEntry`(category: ItemDebugCategory, entry: Component)
    @Suppress("FunctionName")
    fun `skyblockapi$getEntries`(): Multimap<ItemDebugCategory, Component>?
}
