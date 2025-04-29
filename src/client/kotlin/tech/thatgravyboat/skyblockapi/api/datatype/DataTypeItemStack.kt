package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemValueResult

internal interface DataTypeItemStack {

    fun <T> `skyblockapi$getType`(type: DataType<T>): T?

    fun `skyblockapi$getTypes`(): Map<DataType<*>, *>

    fun `skyblockapi$setTypes`(types: Map<DataType<*>, *>)

    fun `skyblockapi$getItemValueResult`(): ItemValueResult?
}

fun ItemStack.getDataTypes(): Map<DataType<*>, *> = (this as? DataTypeItemStack)?.`skyblockapi$getTypes`() ?: mapOf<DataType<*>, Any>()
fun <T> ItemStack.getData(type: DataType<T>): T? = (this as? DataTypeItemStack)?.`skyblockapi$getType`(type)
fun ItemStack.getItemValue(): ItemValueResult = (this as? DataTypeItemStack)?.`skyblockapi$getItemValueResult`() ?: ItemValueResult.EMPTY
