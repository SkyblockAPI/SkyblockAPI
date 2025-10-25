package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry

class DataType<T>(
    val id: String,
    autoRegister: Boolean,
    val factory: (ItemStack) -> T?,
) {
    constructor(id: String, factory: (ItemStack) -> T?) : this(id, true, factory)

    init {
        if (autoRegister) DataTypesRegistry.addDataType(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun cast(value: Any?): T? = value as? T
}
