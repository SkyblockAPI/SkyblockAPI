package tech.thatgravyboat.skyblockapi.utils.builders

import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.level.ItemLike
import tech.thatgravyboat.skyblockapi.api.item.ClickConsumer
import tech.thatgravyboat.skyblockapi.api.item.asVisualItemAccessor
import tech.thatgravyboat.skyblockapi.utils.extentions.holder
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style

class ItemBuilder {
    lateinit var item: Item
    var count: Int = 1
    private var components = DataComponentPatch.builder()
    private var clickAction: ClickConsumer? = null
    var customSlotText: String? = null
    var customSlotComponent: Component? = null
    var backgroundItem: ItemStack? = null
    var backgroundColor: Int = 0
    var foregroundColor: Int = 0
    var borderColor: Int = 0

    companion object {
        operator fun invoke(item: ItemLike, init: ItemBuilder.() -> Unit): ItemStack {
            return ItemBuilder().apply {
                this.item = item.asItem()
                init()
            }.build()
        }
    }

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

        val patch = stack.componentsPatch.split()
        patch.added().forEach { typesComponent ->
            components.set(typesComponent.type as DataComponentType<Any>, typesComponent.value)
        }
        patch.removed().forEach { typesComponent ->
            components.remove(typesComponent)
        }
    }

    private val customItemName: Component?
        get() = components.build().get(DataComponentMap.EMPTY, DataComponents.CUSTOM_NAME)

    fun namePrefix(prefix: String) = namePrefix(Component.literal(prefix))
    fun namePrefix(prefix: Component) = name(Text.join(prefix, customItemName))

    fun name(name: String) = name(Component.literal(name))
    fun name(name: Component) = apply {
        components.set(
            DataComponents.CUSTOM_NAME,
            name.copy().setItalic(),
        )
    }

    fun nameSuffix(suffix: String) = nameSuffix(Component.literal(suffix))
    fun nameSuffix(suffix: Component) = name(Text.join(customItemName, suffix))

    private fun MutableComponent.setItalic() = style { this.withItalic(this@setItalic.style.isItalic) }

    fun tooltip(init: TooltipBuilder.() -> Unit) = apply {
        val builder = TooltipBuilder()
        builder.init()
        components.set(DataComponents.LORE, ItemLore(builder.lines(), builder.lines()))
    }

    /** If [clickAction] returns null, it won't cancel the original click. */
    fun onClick(clickAction: ((Int) -> Unit?)?) {
        this.clickAction = clickAction?.let(::ClickConsumer)
    }

    fun <T : Any> set(type: DataComponentType<T>, value: T?) {
        if (value != null) {
            components.set(type, value)
        } else {
            components.remove(type)
        }
    }

    fun build(): ItemStack {
        return ItemStack(item.holder, count, components.build()).apply {
            this.asVisualItemAccessor().let {
                it.`skyblockapi$setSlotText`(customSlotText?.asComponent() ?: customSlotComponent)
                it.`skyblockapi$setOnClickAction`(clickAction)
                it.`skyblockapi$setBackgroundItem`(backgroundItem)
                it.`skyblockapi$setBackgroundColor`(backgroundColor)
                it.`skyblockapi$setForegroundColor`(foregroundColor)
                it.`skyblockapi$setBorderColor`(borderColor)
            }
        }
    }
}
