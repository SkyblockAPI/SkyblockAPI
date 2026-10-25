package tech.thatgravyboat.skyblockapi.impl

import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.filterValuesNotNull
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public object DataTypesRegistry {

    private var initialized = false
    public val types: List<DataType<*>>
        field: MutableList<DataType<*>> = mutableListOf()

    internal fun load() {
        RegisterDataTypesEvent(types::add).post(SkyBlockAPI.eventBus)
        initialized = true
    }

    internal fun addDataType(dataType: DataType<*>) {
        if (dataType !in types) types.add(dataType)
    }

    // Must check if initialization has happened because mods like skyhanni load itemstacks instantly in preinit
    // and fabric is sequential so they load before we can register our data types
    @ApiStatus.Internal
    public fun getData(item: ItemStack): Map<DataType<*>, *>? = if (initialized) getDataImpl(item) else null

    internal fun getDataImpl(item: ItemStack): Map<DataType<*>, *> = runCatching {
        val context = ResolutionContext(item)
        types
            .associateWith { it.resolve(item, context) }
            .filterValuesNotNull()
            .filterValues { it !is Map<*, *> || it.isNotEmpty() }
            .filterValues { it !is Collection<*> || it.isNotEmpty() }
    }.getOrElse {
        SkyBlockAPI.logger.error("Failed to get data for ${item.hoverName.stripped}", it)
        SkyBlockAPI.logger.error("Item: ${item.toJson(ItemStack.CODEC)}")
        mapOf()
    }
}
