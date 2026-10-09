package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import tech.thatgravyboat.skyblockapi.api.datatype.getDataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.item.calculator.getItemValue
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.getHoveredSlot
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.getTexture
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toNbt
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.appendLine
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.trimLines

@Module
internal object DebugInventory {

    val enabled by debugToggle("inventory", "Lets you copy information from items in inventories.")

    @Subscription
    fun onKeyPressed(event: ScreenKeyPressedEvent.Pre) {
        if (!enabled) return
        val slot = McScreen.asMenu?.getHoveredSlot() ?: return
        val cancel = CopyType.entries.find { it.key == event.key }?.initCopy(slot) ?: false

        if (cancel) event.cancel()
    }

    @Subscription
    fun onForegroundRender(event: RenderScreenForegroundEvent) {
        if (!enabled) return
        val slot = McScreen.asMenu?.getHoveredSlot() ?: return

        Text.of {
            color = TextColor.YELLOW
            appendLine("Slot: ") {
                append(slot.index.toString(), TextColor.AQUA)
            }
            appendLine()
            appendLine("Copy options:")

            CopyType.entries.forEach { entry ->
                append("  ")
                append("[", TextColor.DARK_GRAY)
                append(entry.keyName) { color = TextColor.RED }
                append("] ", TextColor.DARK_GRAY)
                append(entry.title)
                if (entry.extraDescription != null) {
                    append(" (${entry.extraDescription})", TextColor.GRAY)
                }
                appendLine()
            }
        }.trimLines().splitLines().forEachIndexed { index, line ->
            event.graphics.drawString(line, 8, 8 + index * McFont.height)
        }
    }

    enum class CopyType(
        val key: Int,
        val copy: (Slot) -> String?,
        val extraDescription: String? = null,
    ) {
        RAW_ITEM_DATA(
            InputConstants.KEY_R,
            {
                if (McScreen.isShiftDown) {
                    NbtUtils.structureToSnbt(it.item.toNbt(ItemStack.CODEC)?.asCompound()?.get())
                } else {
                    it.item.toJson(ItemStack.CODEC).toPrettyString()
                }
            },
            "Hold Shift for snbt",
        ),
        SKIN(
            InputConstants.KEY_S,
            { it.item.getTexture() },
        ),
        ID(
            InputConstants.KEY_I,
            { it.item.getSkyBlockId() },
        ),
        CUSTOM_DATA(
            InputConstants.KEY_D,
            { it.item.get(DataComponents.CUSTOM_DATA)?.toJson(CustomData.CODEC).toPrettyString() },
        ),
        LORE(
            InputConstants.KEY_L,
            {
                if (McScreen.isShiftDown) {
                    it.item.getLore().toJson(ComponentSerialization.CODEC.listOf()).toPrettyString()
                } else {
                    it.item.getRawLore().joinToString("\n")
                }
            },
            "Hold Shift for components",
        ),
        ITEM_MODEL(
            InputConstants.KEY_M,
            { it.item.get(DataComponents.ITEM_MODEL).toString() },
        ),
        DATA_TYPES(
            InputConstants.KEY_C,
            {
                it.item.getDataTypes().entries.joinToString("\n") { (k, v) -> "${k.id}: ${v.toString()}" }
            },
        ),
        ITEM_VALUE(
            InputConstants.KEY_P,
            { slot ->
                buildString {
                    appendLine("Item Value: ${slot.item.getItemValue().price.toFormattedString()}")
                    appendLine()
                    appendLine("Sources:")
                    slot.item.getItemValue().entryTree.sortedByDescending { it.price }.forEach {
                        appendLine(" ${it.source.name}: ${it.price.toFormattedString()}")
                    }
                }
            },
        ),
        ;

        val title = toFormattedName()

        //~ if >= 26.3 'KEYSYM' -> 'KEYBOARD'
        val keyName: Component = InputConstants.Type.KEYBOARD.getOrCreate(key).displayName

        fun initCopy(slot: Slot): Boolean {
            val data = copy(slot) ?: return false
            McClient.clipboard = data
            Text.sendDebug("Copied item $title to clipboard.")
            return true
        }
    }
}
