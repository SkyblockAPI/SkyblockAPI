package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.core.component.DataComponents
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
import tech.thatgravyboat.skyblockapi.utils.extentions.getHoveredSlot
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.getTexture
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugInventory {

    private var enabled = false

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi inventory") {
            callback {
                enabled = !enabled
                Text.multiline(
                    "[SkyBlockAPI] Debug inventory: $enabled",
                    "Use [C] to copy the raw item data.",
                    "Use [S] to copy the skin.",
                    "Use [I] to copy the id.",
                    "Use [D] to copy the custom data.",
                ) {
                    this.color = TextColor.YELLOW
                }.send()
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
