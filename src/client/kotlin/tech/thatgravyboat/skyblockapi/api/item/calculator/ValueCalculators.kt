package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing

interface Calculator {
    fun calculate(id: String, stack: ItemStack): List<CalculationEntry>?
}

interface SingleEntryCalculator : Calculator {

    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? = getEntry(id, stack)?.let { listOf(it) }

    fun getEntry(id: String, stack: ItemStack): CalculationEntry?
}

interface MultiEntryCalculator : Calculator {

    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val entries = getEntries(id, stack) ?: return null
        return entries.ifEmpty { null }
    }

    fun getEntries(id: String, stack: ItemStack): List<CalculationEntry>?
}

open class DataTypeCalculator(private val dataType: DataType<String>) : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val data = stack.getData(dataType) ?: return null
        return ItemEntry(data)
    }
}

open class DataTypeListCalculator(private val dataType: DataType<List<String>>) : MultiEntryCalculator {
    override fun getEntries(id: String, stack: ItemStack): List<CalculationEntry>? {
        val data = stack.getData(dataType) ?: return null
        if (data.isEmpty()) return null

        return data.map { ItemEntry(it) }
    }
}

open class DataTypesCalculator(private vararg val dataTypes: DataType<String>) : Calculator {
    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val data = dataTypes.mapNotNull { stack.getData(it) }.takeUnless { it.isEmpty() } ?: return null

        return data.map { ItemEntry(it) }
    }
}

open class IntDataTypeCalculator(private val dataType: DataType<Int>, private val itemId: String) : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val amount = stack.getData(dataType) ?: return null

        return ItemEntry(itemId, Pricing.getPrice(itemId) * amount, amount)
    }
}

open class BoolDataTypeCalculator(private val dataType: DataType<Boolean>, private val itemId: String) : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val value = stack.getData(dataType) ?: return null

        return if (value) ItemEntry(itemId) else null
    }
}
