package tech.thatgravyboat.skyblockapi.utils.builders

import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.item.asVisualItemAccessor
import tech.thatgravyboat.skyblockapi.api.item.replaceVisually
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.holder
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
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

    private val customItemName get() = components.build().get(DataComponents.CUSTOM_NAME)?.getOrNull()

    fun namePrefix(prefix: String) = namePrefix(Component.literal(prefix))
    fun namePrefix(prefix: Component) = apply {
        components.set(
            DataComponents.CUSTOM_NAME,
            Text.join(
                prefix,
                customItemName?.copy()?.setItalic() ?: CommonText.EMPTY,
            ),
        )
    }


    fun name(name: String) = name(Component.literal(name))
    fun name(name: Component) = apply {
        components.set(
            DataComponents.CUSTOM_NAME,
            name.copy().setItalic(),
        )
    }

    fun nameSuffix(suffix: String) = nameSuffix(Component.literal(suffix))
    fun nameSuffix(suffix: Component) = apply {
        components.set(
            DataComponents.CUSTOM_NAME,
            Text.join(
                customItemName?.copy()?.setItalic() ?: CommonText.EMPTY,
                suffix,
            ),
        )
    }

    private fun MutableComponent.setItalic() = style {
        if (!this.isItalic) {
            return@style this.withItalic(false)
        }
        this
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

@Module
object Pest {
    val regex = "ൠ This plot has (?<amount>.*) Pests?!".toRegex()

    @Subscription
    @InventoryTitle("Configure Plots")
    fun onInv(event: InventoryChangeEvent) {
        regex.anyMatch(event.item.getRawLore(), "amount") { (amount) ->
            val amount = amount.toIntValue().takeUnless { it == 0 } ?: return@anyMatch
            event.item.replaceVisually {
                copyFrom(event.item)
                namePrefix("testing")
                nameSuffix("aaaaaa")
                backgroundItem = Items.LIME_STAINED_GLASS_PANE.defaultInstance
                customSlotText = "$amount"
            }
        }
    }
}
