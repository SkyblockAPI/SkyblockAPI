package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing

//~ if >= 26.4 'public' -> 'internal'
public interface Calculator {
    public fun calculate(id: String, stack: ItemStack): List<CalculationEntry>?
}

//~ if >= 26.4 'public' -> 'internal'
public interface SingleEntryCalculator : Calculator {

    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? = getEntry(id, stack)?.let { listOf(it) }

    public fun getEntry(id: String, stack: ItemStack): CalculationEntry?
}

//~ if >= 26.4 'public' -> 'internal'
public interface MultiEntryCalculator : Calculator {

    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val entries = getEntries(id, stack) ?: return null
        return entries.ifEmpty { null }
    }

    public fun getEntries(id: String, stack: ItemStack): List<CalculationEntry>?
}

//~ if >= 26.4 'public' -> 'internal'
public open class DataTypeCalculator(private val dataType: DataType<String>) : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val data = stack.getData(dataType) ?: return null
        return ItemEntry(data)
    }
}

//~ if >= 26.4 'public' -> 'internal'
public open class DataTypeListCalculator(private val dataType: DataType<List<String>>) : MultiEntryCalculator {
    override fun getEntries(id: String, stack: ItemStack): List<CalculationEntry>? {
        val data = stack.getData(dataType) ?: return null
        if (data.isEmpty()) return null

        return data.map { ItemEntry(it) }
    }
}

//~ if >= 26.4 'public' -> 'internal'
public open class DataTypesCalculator(private vararg val dataTypes: DataType<String>) : Calculator {
    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val data = dataTypes.mapNotNull { stack.getData(it) }.takeUnless { it.isEmpty() } ?: return null

        return data.map { ItemEntry(it) }
    }
}

//~ if >= 26.4 'public' -> 'internal'
public open class IntDataTypeCalculator(private val dataType: DataType<Int>, private val itemId: String) : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val amount = stack.getData(dataType) ?: return null

        return ItemEntry(itemId, Pricing.getPrice(itemId) * amount, amount)
    }
}

//~ if >= 26.4 'public' -> 'internal'
public open class IntDataTypeWithLimitCalculator(private val dataType: DataType<Int>, private val itemId: String, private val limit: Int) : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val amount = stack.getData(dataType) ?: return null

        return ItemWithLimitEntry(itemId, Pricing.getPrice(itemId) * amount, amount, limit)
    }
}

//~ if >= 26.4 'public' -> 'internal'
public open class BoolDataTypeCalculator(private val dataType: DataType<Boolean>, private val itemId: String) : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val value = stack.getData(dataType) ?: return null

        return if (value) ItemEntry(itemId) else null
    }
}

internal open class BoolDataTypeItemListCalculator(private val dataType: DataType<Boolean>, private val itemIds: List<String>) : MultiEntryCalculator {
    override fun getEntries(id: String, stack: ItemStack, ): List<CalculationEntry>? {
        val value = stack.getData(dataType) ?: return null
        return if (value) itemIds.map { ItemEntry(it) } else null
    }
}

//~ if >= 26.4 'public' -> 'internal'
public open class SkyBlockIdDataTypeCalculator(private val dataType: DataType<SkyBlockId>) : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val value = stack.getData(dataType) ?: return null

        return ItemEntry(value.skyblockId)
    }
}
