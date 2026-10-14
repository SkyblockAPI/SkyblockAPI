package tech.thatgravyboat.skyblockapi.api.events.misc

import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public class RegisterDataTypesEvent(private val registry: (DataType<*>) -> Unit) : SkyBlockEvent() {

    public fun register(type: DataType<*>) {
        registry(type)
    }
}
