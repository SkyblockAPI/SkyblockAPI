package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.ItemStack

internal interface DataTypeItemStack {

    fun <T> `skyblockapi$getType`(type: DataType<T>): T?

    fun `skyblockapi$getTypes`(): Map<DataType<*>, *>
}

public fun ItemStack.getDataTypes(): Map<DataType<*>, *> =
    @Suppress("CAST_NEVER_SUCCEEDS")
    (this as? DataTypeItemStack)?.`skyblockapi$getTypes`() ?: mapOf<DataType<*>, Any>()

public fun <T> ItemStack.getData(type: DataType<T>): T? =
    @Suppress("CAST_NEVER_SUCCEEDS")
    (this as? DataTypeItemStack)?.`skyblockapi$getType`(type)
