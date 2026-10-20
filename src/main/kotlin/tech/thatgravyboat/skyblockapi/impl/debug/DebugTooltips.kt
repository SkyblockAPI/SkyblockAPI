package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getDataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemDebugTooltipEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemTooltipEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.item.calculator.getItemValue
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry
import tech.thatgravyboat.skyblockapi.utils.debugMultiSelect
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.math.max

@Module
public object DebugTooltips {

    private var lastItem = 0
    private var keys = mutableListOf<DataType<*>>()
    private var lastDataType: DataType<*>? = null
    private var index = 0

    private val toggle: Boolean by debugToggle("tooltips", "Adds DataType debug information to advanced tooltips")
    private val isEnabled: Boolean get() = McClient.isDev || toggle

    private val alwaysShow: Set<String> by debugMultiSelect(
        path = "always_show_datatypes",
        description = "DataTypes to always show in item tooltips",
    ) { DataTypesRegistry.types.map { it.id } }

    @Subscription
    internal fun onKeyPressed(event: ScreenKeyPressedEvent.Pre) {
        if (!isEnabled) return
        if (keys.isEmpty()) return
        if (!McScreen.isAltDown) return

        index = when (event.key) {
            InputConstants.KEY_RIGHT -> (index + 1) % keys.size
            InputConstants.KEY_LEFT -> if (index == 0) keys.size - 1 else index - 1
            else -> return
        }
        lastDataType = keys[index]
    }

    @Subscription(priority = Subscription.LOWEST)
    internal fun onGetItemTooltip(event: ItemTooltipEvent) {
        if (alwaysShow.isEmpty()) return
        val types = event.item.getDataTypes()
        if (types.isEmpty()) return

        var addedHeader = false

        for ((key, value) in types) {
            if (key.id in alwaysShow) {
                if (!addedHeader) {
                    event.add(CommonText.EMPTY)
                    addedHeader = true
                }
                appendDataType(event.tooltip, key, value, withDash = false)
            }
        }
    }

    @Subscription
    internal fun onGetDebugTooltip(event: ItemDebugTooltipEvent) {
        if (!isEnabled) return
        val types = event.item.getDataTypes()
        if (types.isEmpty()) return

        val hash = System.identityHashCode(event.item)
        if (hash != lastItem) {
            keys = types.keys.toMutableList().apply {
                if (remove(DataTypes.SKYBLOCK_ID)) {
                    addFirst(DataTypes.SKYBLOCK_ID)
                }
                remove(DataTypes.RAW_LORE)
            }
            index = 0
            lastItem = hash
            val lastDataType = lastDataType
            if (lastDataType != null) {
                index = max(keys.indexOf(lastDataType), 0)
            }
        }

        event.add(CommonText.EMPTY)

        if (!McScreen.isAltDown) {
            event.add(Text.of("${keys.size} Data Type(s) [Alt]", TextColor.DARK_GRAY))
            lastDataType = null
            index = 0
        } else {
            event.add(
                Text.join(
                    Text.of("${index + 1}/${types.size} Data Type(s) [", TextColor.DARK_GRAY),
                    Text.of("ALT") {
                        this.bold = true
                        this.color = TextColor.GRAY
                    },
                    Text.of("]", TextColor.DARK_GRAY),
                ),
            )

            val key = keys[index]
            val value = types[key]

            appendDataType(event.tooltip, key, value)

            event.add(CommonText.EMPTY)

            val itemValue = event.item.getItemValue()
            event.add(Text.of("Value: ${itemValue.rawPrice.toFormattedString()}-${itemValue.price.toFormattedString()}", TextColor.DARK_GRAY))
            event.add(Text.of("Sources: ([Shift] for all)", TextColor.DARK_GRAY))

            val sources = itemValue.entryTree.sortedByDescending { it.price }
            val sourcesToShow = sources.filter { it.price > 0L }.takeUnless { McScreen.isShiftDown } ?: sources

            sourcesToShow.map { entry ->
                Text.join(
                    Text.of(" - ${entry.source.name}: ", TextColor.DARK_GRAY),
                    Text.of(entry.price.toFormattedString(), TextColor.GRAY),
                )
            }.forEach { event.add(it) }
        }
    }

    private fun appendDataType(tooltip: MutableList<Component>, key: DataType<*>, value: Any?, withDash: Boolean = true) {
        if (value is List<*>) {
            tooltip.add(
                Text.join(
                    if (withDash) Text.of(" - ", TextColor.DARK_GRAY) else null,
                    Text.of("${key.id}: ", TextColor.DARK_GRAY),
                    Text.of("[", TextColor.GRAY),
                ),
            )
            for (any in value) {
                tooltip.add(Text.of("   $any,", TextColor.GRAY))
            }
            tooltip.add(Text.of("]", TextColor.GRAY))
        } else {
            tooltip.add(
                Text.join(
                    if (withDash) Text.of(" - ", TextColor.DARK_GRAY) else null,
                    Text.of("${key.id}: ", TextColor.DARK_GRAY),
                    Text.of("$value", TextColor.GRAY),
                ),
            )
        }
    }
}
