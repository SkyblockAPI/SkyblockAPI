package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.core.component.DataComponents
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getHoveredSlot
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
        event.register("sbapi") {
            then("inventory") {
                callback {
                    enabled = !enabled
                    Text.multiline(
                        "[SkyBlockAPI] Debug inventory: $enabled",
                        "Use [C] to copy the raw item data.",
                        "Use [S] to copy the skin.",
                    ) {
                        this.color = TextColor.YELLOW
                    }.send()
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
            else -> false
        }

        if (cancel) event.cancel()
    }

    private fun copySkin(slot: Slot): Boolean {
        val item = slot.item
        if (!item.`is`(Items.PLAYER_HEAD)) {
            Text.of("This item is not a player head.").send()
            return false
        }

        val skin = item.get(DataComponents.PROFILE) ?: run {
            Text.of("This item does not have a skin.").send()
            return false
        }
        skin.gameProfile.properties.get("textures").first().value.let {
            McClient.clipboard = it
            Text.debug("Copied skin data to clipboard.").send()
        }
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
