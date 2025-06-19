package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import tech.thatgravyboat.skyblockapi.api.datatype.getDataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
internal object DebugInventory {

    private var enabled = false

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi inventory") {
            callback {
                enabled = !enabled
                Text.of("[SkyBlockAPI] Debug inventory: ") {
                    append(enabled) {
                        this.color = if (enabled) TextColor.GREEN else TextColor.RED
                    }

                    this.color = TextColor.YELLOW
                }.send()
                if (!enabled) return@callback

                CopyType.entries.forEach {
                    Text.of {
                        append("Press ")
                        append("[") { this.color = TextColor.GOLD }
                        append(it.keyName) { this.color = TextColor.AQUA }
                        append("]") { this.color = TextColor.GOLD }
                        append(" to copy the ")
                        append(it.title) { this.color = TextColor.YELLOW }
                        append(".")
                    }.send()
                }
            }
        }
    }

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
        val menuScreen = McScreen.asMenu ?: return
        val slot = menuScreen.getHoveredSlot() ?: return

        buildList {
            add("Slot: ${slot.index}")
            add("")
            add("Copy options:")
            CopyType.entries.forEach {
                add("  [${it.keyName.stripped}] ${it.title} ${it.extraDescription?.let { d -> "($d)" } ?: ""}")
            }
        }.forEachIndexed { index, line ->
            event.graphics.drawString(
                McFont.self,
                line,
                8,
                8 + index * McFont.height,
                0xFFFFFF,
            )
        }
    }

    enum class CopyType(
        val key: Int,
        val copy: (Slot) -> String?,
        val extraDescription: String? = null,
    ) {
        RAW_ITEM_DATA(
            InputConstants.KEY_R,
            { it.item.toJson(ItemStack.CODEC).toPrettyString() },
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
                if (Screen.hasShiftDown()) {
                    it.item.getRawLore().joinToString("\n")
                } else {
                    it.item.getLore().toJson(ComponentSerialization.CODEC.listOf()).toPrettyString()
                }
            },
            "Hold Shift for raw lore",
        ),
        DATA_COMPONENT(
            InputConstants.KEY_C,
            {
                it.item.getDataTypes().map { (k, v) -> "${k.id}: ${v.toString()}" }.joinToString("\n")
            },
        )
        ;

        val title = name.toTitleCase()
        val keyName: Component = InputConstants.getKey(key, -1).displayName

        fun initCopy(slot: Slot): Boolean {
            val data = copy(slot) ?: return false
            McClient.clipboard = data
            Text.debug("Copied item $title to clipboard.").send()
            return true
        }
    }

}
