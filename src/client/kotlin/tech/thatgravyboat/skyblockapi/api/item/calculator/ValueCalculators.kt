package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.getData

interface Calculator {
    fun calculate(id: String, stack: ItemStack): Long
}

open class DataTypeCalculator(private val dataType: DataType<String>) : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        return Pricing.getPrice(stack.getData(dataType) ?: return 0L)
    }
}

open class DataTypesCalculator(private vararg val dataTypes: DataType<String>) : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        return dataTypes.sumOf { Pricing.getPrice(stack.getData(it) ?: return@sumOf 0L) }
    }
}

open class IntDataTypeCalculator(private val dataType: DataType<Int>, private val itemId: String) : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        return Pricing.getPrice(itemId) * (stack.getData(dataType) ?: 0)
    }
}
