package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GenericDataTypes
import tech.thatgravyboat.skyblockapi.utils.extentions.computeRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.getStringOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.unsafeTag

class ResolutionContext internal constructor(private val stack: ItemStack) {

    private val context: MutableMap<String, Any?> = mutableMapOf()

    @OptIn(ExperimentalStdlibApi::class)
    operator fun <T> get(resolver: Resolver<T>): T {
        return this.context.getOrPutIfMissing(resolver.key) {
            resolver.resolve(this.stack)
        } as T
    }

    class Resolver<T> private constructor(
        val key: String,
        private val resolver: (ItemStack) -> T,
    ) {

        fun resolve(stack: ItemStack): T = this.resolver.invoke(stack)

        companion object {

            val RAW_LORE: Resolver<List<String>> = Resolver("raw_lore", ItemStack::computeRawLore)
            val ID: Resolver<String?> = Resolver("id") { it.unsafeTag?.getStringOrNull("id") }
            val PET_DATA: Resolver<GenericDataTypes.PetData?> = Resolver("pet_data", GenericDataTypes.PetData::fromItem)
        }
    }
}
