package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.common.collect.Multimap
import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemDebugTooltipEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
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

@Suppress("CAST_NEVER_SUCCEEDS")
fun ItemStack.getEntries(): Multimap<ItemDebugCategory, Component>? = (this as? ItemDebugAccessor)?.`skyblockapi$getEntries`()

@Suppress("CAST_NEVER_SUCCEEDS")
fun ItemStack.addDebug(category: ItemDebugCategory, entry: () -> Component) {
    if (!DebugItems.isEnabled) return
    (this as? ItemDebugAccessor)?.`skyblockapi$addEntry`(category, entry())
}
fun ItemStack.addStringDebug(category: ItemDebugCategory, entry: () -> String) = addDebug(category) { Component.literal(entry()) }

@JvmName("categoryAddDebug")
context(category: ItemDebugCategory) fun ItemStack.addDebug(entry: () -> Component) = addDebug(category, entry)

@JvmName("categoryAddDebugString")
context(category: ItemDebugCategory) fun ItemStack.addDebugString(entry: () -> String) = addStringDebug(category, entry)

interface ItemDebugCategory {
    override fun toString(): String
}

interface ItemDebugAccessor {
    fun `skyblockapi$addEntry`(category: ItemDebugCategory, entry: Component)
    fun `skyblockapi$getEntries`(): Multimap<ItemDebugCategory, Component>?
}
