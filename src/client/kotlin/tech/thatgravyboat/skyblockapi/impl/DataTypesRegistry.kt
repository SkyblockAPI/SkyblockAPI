package tech.thatgravyboat.skyblockapi.impl

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent

object DataTypesRegistry {

    private val types: MutableList<DataType<*>> = mutableListOf()

    internal fun load() {
        RegisterDataTypesEvent(types::add).post(SkyBlockAPI.eventBus)
    }

    internal fun getData(item: ItemStack): Map<DataType<*>, *> = runCatching {
        types
            .associateWith { it.factory(item) }
            .filterValues { it != null }
    }.getOrElse {
        SkyBlockAPI.logger.error("Faileed to get data for {}", this, it)
        mapOf()
    }
}
