package tech.thatgravyboat.skyblockapi.impl

import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.filterValuesNotNull
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object DataTypesRegistry {

    private var initialized = false
    private val _types: MutableList<DataType<*>> = mutableListOf()
    val types: List<DataType<*>> get() = _types

    internal fun load() {
        RegisterDataTypesEvent(_types::add).post(SkyBlockAPI.eventBus)
        initialized = true
    }

    internal fun addDataType(dataType: DataType<*>) {
        if (dataType !in _types) _types.add(dataType)
    }

    // Must check if initialization has happened because mods like skyhanni load itemstacks instantly in preinit
    // and fabric is sequential so they load before we can register our data types
    @ApiStatus.Internal
    fun getData(item: ItemStack): Map<DataType<*>, *>? = if (initialized) getDataImpl(item) else null

    internal fun getDataImpl(item: ItemStack): Map<DataType<*>, *> = runCatching {
        _types
            .associateWith { it.factory(item) }
            .filterValuesNotNull()
            .filterValues { if (it is Map<*, *>) it.isNotEmpty() else true }
            .filterValues { if (it is Collection<*>) it.isNotEmpty() else true }
    }.getOrElse {
        SkyBlockAPI.logger.error("Failed to get data for ${item.hoverName.stripped}", it)
        SkyBlockAPI.logger.error("Item: ${item.toJson(ItemStack.CODEC)}")
        mapOf()
    }
}
