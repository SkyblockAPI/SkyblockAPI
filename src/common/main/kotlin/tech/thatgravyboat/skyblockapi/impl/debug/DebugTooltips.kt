package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getDataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemDebugTooltipEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.item.calculator.getItemValue
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugTooltips {

    private var lastItem = 0
    private var keys = mutableListOf<DataType<*>>()
    private var lastDataType: DataType<*>? = null
    private var index = 0

    private val toggle: Boolean by debugToggle("tooltips", "Adds DataType debug information to advanced tooltips")
    private val isEnabled: Boolean get() = McClient.isDev || toggle

    @Subscription
    fun onKeyPressed(event: ScreenKeyPressedEvent.Pre) {
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

    @Subscription
    fun onGetDebugTooltip(event: ItemDebugTooltipEvent) {
        if (!isEnabled) return
        val types = event.item.getDataTypes()
        if (types.isEmpty()) return

        val hash = System.identityHashCode(event.item)
        if (hash != lastItem) {
            keys = types.keys.toMutableList().apply {
                if (remove(DataTypes.SKYBLOCK_ID)) {
                    addFirst(DataTypes.SKYBLOCK_ID)
                }
            }
            index = 0
            lastItem = hash
            val lastDataType = lastDataType
            if (lastDataType != null) {
                index = keys.indexOf(lastDataType).takeIf { it >= 0 } ?: 0
            }
        }

        event.add(CommonText.EMPTY)

        if (!McScreen.isAltDown) {
            event.add(
                Text.of("${types.size} Data Type(s) [Alt]") {
                    this.color = TextColor.DARK_GRAY
                },
            )
            lastDataType = null
            index = 0
        } else {
            event.add(
                Text.join(
                    Text.of("${index + 1}/${types.size} Data Type(s) [") { this.color = TextColor.DARK_GRAY },
                    Text.of("ALT") {
                        this.bold = true
                        this.color = TextColor.GRAY
                    },
                    Text.of("]") { this.color = TextColor.DARK_GRAY },
                ),
            )

            val key = keys[index]
            val value = types[key]

            if (value is List<*>) {
                event.add(
                    Text.join(
                        Text.of(" - ${key.id}: ") { this.color = TextColor.DARK_GRAY },
                        Text.of("[") { this.color = TextColor.GRAY },
                    ),
                )
                for (any in value) {
                    event.add(Text.join(Text.of("   $any,") { this.color = TextColor.GRAY }))
                }
                event.add(Text.of("]") { this.color = TextColor.GRAY })
            } else {
                event.add(
                    Text.join(
                        Text.of(" - ${key.id}: ") { this.color = TextColor.DARK_GRAY },
                        Text.of("$value") { this.color = TextColor.GRAY },
                    ),
                )
            }

            event.add(CommonText.EMPTY)

            val itemValue = event.item.getItemValue()
            event.add(
                Text.of("Value: ${itemValue.rawPrice.toFormattedString()}-${itemValue.price.toFormattedString()}") {
                    this.color = TextColor.DARK_GRAY
                },
            )
            event.add(Text.of("Sources: ([Shift] for all)") { this.color = TextColor.DARK_GRAY })

            val sources = itemValue.entryTree.sortedByDescending { it.price }
            val sourcesToShow = sources.filter { it.price > 0L }.takeUnless { McScreen.isShiftDown } ?: sources

            sourcesToShow.map { entry ->
                Text.join(
                    Text.of(" - ${entry.source.name}: ") { this.color = TextColor.DARK_GRAY },
                    Text.of(entry.price.toFormattedString()) { this.color = TextColor.GRAY },
                )
            }.forEach { event.add(it) }

        }
    }
}
