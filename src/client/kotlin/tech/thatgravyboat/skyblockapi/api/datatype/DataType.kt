package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.ItemStack

class DataType<T>(
    val id: String,
    val factory: (ItemStack) -> T?
) {

    @Suppress("UNCHECKED_CAST")
    fun cast(value: Any?): T? = value as? T
}
