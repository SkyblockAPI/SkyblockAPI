package tech.thatgravyboat.skyblockapi.api.item

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder

internal fun interface ClickConsumer {
    fun accept(button: Int): Unit?
}

internal interface VisualItemAccessor {
    fun `skyblockapi$setVisualItem`(item: ItemStack?)
    fun `skyblockapi$getVisualItem`(): ItemStack?
    fun `skyblockapi$setSlotText`(slotText: String?)
    fun `skyblockapi$getSlotText`(): String?
    fun `skyblockapi$setOnClickAction`(clickAction: ClickConsumer?)
    fun `skyblockapi$getOnClickAction`(): ClickConsumer?
    fun `skyblockapi$setBackgroundItem`(item: ItemStack?)
    fun `skyblockapi$getBackgroundItem`(): ItemStack?

    companion object {
        fun getVisualItemAccessor(item: ItemStack?): VisualItemAccessor {
            @Suppress("CAST_NEVER_SUCCEEDS")
            return item as VisualItemAccessor
        }
    }
}

internal fun ItemStack.asVisualItemAccessor(): VisualItemAccessor {
    return VisualItemAccessor.getVisualItemAccessor(this)
}

fun ItemStack.replaceVisually(builder: ItemBuilder.() -> Unit) = replaceVisually(ItemBuilder().apply { builder() }.build())

fun ItemStack.replaceVisually(replacement: ItemStack?) {
    this.asVisualItemAccessor().`skyblockapi$setVisualItem`(replacement)
}

internal fun ItemStack.getClickAction() = this.asVisualItemAccessor().`skyblockapi$getOnClickAction`()
fun ItemStack.getVisualItem() = this.asVisualItemAccessor().`skyblockapi$getVisualItem`()

