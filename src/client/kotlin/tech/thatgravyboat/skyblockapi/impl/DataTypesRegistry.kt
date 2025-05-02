package tech.thatgravyboat.skyblockapi.impl

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object DataTypesRegistry {

    private val types: MutableList<DataType<*>> = mutableListOf()

    internal fun load() {
        RegisterDataTypesEvent(types::add).post(SkyBlockAPI.eventBus)
    }

    internal fun getData(item: ItemStack): Map<DataType<*>, *> = runCatching {
        types
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
