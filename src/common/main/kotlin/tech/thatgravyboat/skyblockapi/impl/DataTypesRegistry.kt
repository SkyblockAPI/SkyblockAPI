package tech.thatgravyboat.skyblockapi.impl

import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object DataTypesRegistry {

    private val _types: MutableList<DataType<*>> = mutableListOf()
    val types: List<DataType<*>> get() = _types

    internal fun load() {
        RegisterDataTypesEvent(_types::add).post(SkyBlockAPI.eventBus)
    }

    internal fun addDataType(dataType: DataType<*>) {
        if (dataType !in _types) _types.add(dataType)
    }

    @ApiStatus.Internal
    fun getData(item: ItemStack) = getDataImpl(item)

    internal fun getDataImpl(item: ItemStack): Map<DataType<*>, *> = runCatching {
        _types
            .associateWith { it.factory(item) }
            .filterValues { if (it is Map<*, *>) it.isNotEmpty() else true }
            .filterValues { if (it is Collection<*>) it.isNotEmpty() else true }
            .filterValues { it != null }
    }.getOrElse {
        SkyBlockAPI.logger.error("Failed to get data for ${item.hoverName.stripped}", it)
        SkyBlockAPI.logger.error("Item: ${item.toJson(ItemStack.CODEC)}")
        mapOf()
    }
}
