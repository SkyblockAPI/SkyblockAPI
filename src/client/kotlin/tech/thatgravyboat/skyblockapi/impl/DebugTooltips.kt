package tech.thatgravyboat.skyblockapi.impl

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.getDataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemDebugTooltipEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugTooltips {

    private var lastItem = 0
    private var keys = emptyList<DataType<*>>()
    private var index = 0

    @Subscription
    fun onKeyPressed(event: ScreenKeyPressedEvent.Pre) {
        if (keys.isEmpty()) return
        if (!Screen.hasAltDown()) return

        when (event.key) {
            InputConstants.KEY_RIGHT -> index = (index + 1) % keys.size
            InputConstants.KEY_LEFT -> index = if (index == 0) keys.size - 1 else index - 1
        }
    }

    @Subscription
    fun onGetDebugTooltip(event: ItemDebugTooltipEvent) {
        val types = event.item.getDataTypes()
        if (types.isEmpty()) return

        val hash = System.identityHashCode(event.item)
        if (hash == lastItem) {
            keys = types.keys.toList()
            index = 0
            lastItem = hash
        }

        event.add(CommonText.EMPTY)

        if (!Screen.hasAltDown()) {
            event.add(Text.of("${types.size} Data Type(s) [Alt]") {
                this.color = TextColor.DARK_GRAY
            })
            keys = types.keys.toList()
            index = 0
        } else {
            event.add(Text.join(
                Text.of("${index + 1}/${types.size} Data Type(s) [") { this.color = TextColor.DARK_GRAY },
                Text.of("ALT") {
                    this.bold = true
                    this.color = TextColor.GRAY
                },
                Text.of("]") { this.color = TextColor.DARK_GRAY }
            ))

            if (keys.isEmpty()) keys = types.keys.toList()

            if (keys.isNotEmpty()) {
                val key = keys[index]
                val value = types[key]
                event.add(Text.join(
                    Text.of(" - ${key.id}: ") { this.color = TextColor.DARK_GRAY },
                    Text.of("$value") { this.color = TextColor.GRAY }
                ))
            }
        }
    }
}
