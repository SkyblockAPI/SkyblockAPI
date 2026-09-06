package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore

class ResolutionContext internal constructor(private val stack: ItemStack) {

    private val context: MutableMap<String, Any?> = mutableMapOf()

    operator fun <T> get(resolver: Resolver<T>): T {
        return this.context.getOrPut(resolver.key) {
            resolver.resolve(this.stack)
        } as T
    }

    class Resolver<T> private constructor(
        val key: String,
        private val resolver: (ItemStack) -> T,
    ) {

        fun resolve(stack: ItemStack): T = this.resolver.invoke(stack)

        companion object {

            val RAW_LORE = Resolver("raw_lore", ItemStack::getRawLore)
        }
    }
}
