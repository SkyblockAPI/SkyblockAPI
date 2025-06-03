package tech.thatgravyboat.skyblockapi.utils.builders

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.skyblockapi.api.item.asVisualItemAccessor
import tech.thatgravyboat.skyblockapi.utils.extentions.holder
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style
import kotlin.jvm.optionals.getOrNull

class ItemBuilder {
    lateinit var item: Item
    var count: Int = 1
    private var components = DataComponentPatch.builder()
    private var clickAction: ((Int) -> Unit)? = null
    var customSlotText: String? = null
    var backgroundItem: ItemStack? = null

    /**
     * Copies the state of the stack to this builder. Replacing existing components, item, and count.
     */
    fun copyFrom(data: ItemStack) = apply {
        this.components = DataComponentPatch.builder()
        this.applyFrom(data)
    }

    /**
     * Applies the state of the stack to this builder. Keeping existing components if already set but replacing item and count.
     */
    fun applyFrom(stack: ItemStack) = apply {
        this.item = stack.item
        this.count = stack.count

        val patch = stack.componentsPatch.entrySet()
        patch.forEach { (type, data) ->
            val data = data.getOrNull() ?: return@forEach
            @Suppress("UNCHECKED_CAST")
            components.set(type as DataComponentType<Any>, data)
        }
    }

    fun name(name: String) = name(Component.literal(name))
    fun name(name: Component) = apply {
        components.set(
            DataComponents.CUSTOM_NAME,
            name.copy().style {
                if (!this.isItalic) {
                    return@style this.withItalic(false)
                }
                this
            },
        )
    }

    fun tooltip(init: TooltipBuilder.() -> Unit) = apply {
        val builder = TooltipBuilder()
        builder.init()
        components.set(DataComponents.LORE, ItemLore(builder.lines(), builder.lines()))
    }

    fun onClick(clickAction: ((Int) -> Unit)?) {
        this.clickAction = clickAction
    }

    fun <T> set(type: DataComponentType<T>, value: T?) {
        if (value != null) {
            components.set(type, value)
        } else {
            components.remove(type)
        }
    }

    fun build(): ItemStack {
        return ItemStack(item.holder, count, components.build()).apply {
            this.asVisualItemAccessor().let {
                it.`skyblockapi$setSlotText`(customSlotText)
                it.`skyblockapi$setOnClickAction`(clickAction)
                it.`skyblockapi$setBackgroundItem`(backgroundItem)
            }
        }
    }
}
