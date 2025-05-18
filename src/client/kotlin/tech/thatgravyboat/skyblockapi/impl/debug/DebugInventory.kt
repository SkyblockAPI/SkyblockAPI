package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugInventory {

    private var enabled = false

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        fun sendKey(key: String, copy: String) {
            Text.of {
                append("Use ")
                append("[") { this.color = TextColor.GOLD }
                append(key) { this.color = TextColor.AQUA }
                append("]") { this.color = TextColor.GOLD }
                append(" to copy the ")
                append(copy) { this.color = TextColor.YELLOW }
                append(".")
            }.send()
        }

        event.register("sbapi inventory") {
            callback {
                enabled = !enabled
                Text.of("[SkyBlockAPI] Debug inventory: ") {
                    append(enabled) {
                        this.color = if (enabled) TextColor.GREEN else TextColor.RED
                    }

                    this.color = TextColor.YELLOW
                }.send()
                if (enabled) {
                    sendKey("C", "raw item data")
                    sendKey("S", "skin")
                    sendKey("I", "id")
                    sendKey("D", "custom data")
                    sendKey("A", "description")
                }
            }
        }
    }

    @Subscription
    fun onKeyPressed(event: ScreenKeyPressedEvent.Pre) {
        if (!enabled) return
        val slot = McScreen.asMenu?.getHoveredSlot() ?: return
        val cancel = when (event.key) {
            InputConstants.KEY_S -> copySkin(slot)
            InputConstants.KEY_C -> copyItem(slot)
            InputConstants.KEY_I -> copyId(slot)
            InputConstants.KEY_D -> copyCustomData(slot)
            InputConstants.KEY_A -> copyDescription(slot)
            else -> false
        }

        if (cancel) event.cancel()
    }

    private fun copyCustomData(slot: Slot): Boolean {
        McClient.clipboard = slot.item.get(DataComponents.CUSTOM_DATA)?.toJson(CustomData.CODEC).toPrettyString()
        Text.debug("Copied custom data to clipboard.").send()
        return true
    }

    private fun copyId(slot: Slot): Boolean {
        McClient.clipboard = slot.item.getSkyBlockId()
        Text.debug("Copied item id to clipboard.").send()
        return true
    }

    private fun copySkin(slot: Slot): Boolean {
        val texture = slot.item.getTexture() ?: run {
            Text.debug("Unable to get Texture of Item").send()
            return false
        }

        McClient.clipboard = texture
        Text.debug("Copied skin data to clipboard.").send()
        return true
    }

    private fun copyItem(slot: Slot): Boolean {
        McClient.clipboard = slot.item.toJson(ItemStack.CODEC).toPrettyString()
        Text.debug("Copied item data to clipboard.").send()
        return true
    }

    private fun copyDescription(slot: Slot): Boolean {
        if (Screen.hasShiftDown()) {
            McClient.clipboard = slot.item.getRawLore().joinToString("\n")
            Text.debug("Copied item description to clipboard. (raw)").send()
        } else {
            McClient.clipboard = slot.item.getLore().toJson(ComponentSerialization.CODEC.listOf()).toPrettyString()
            Text.debug("Copied item description to clipboard.").send()
        }
        return true
    }

    @Subscription
    fun onForegroundRender(event: RenderScreenForegroundEvent) {
        if (!enabled) return
        val menuScreen = McScreen.asMenu ?: return
        val slot = menuScreen.getHoveredSlot() ?: return

        event.graphics.drawCenteredString(
            McFont.self,
            "${slot.index}",
            8,
            8,
            0xFFFFFF,
        )
    }

}
