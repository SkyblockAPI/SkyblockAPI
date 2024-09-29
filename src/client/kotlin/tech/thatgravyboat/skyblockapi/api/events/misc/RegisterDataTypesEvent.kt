package tech.thatgravyboat.skyblockapi.api.events.misc

import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

class RegisterDataTypesEvent(private val registry: (DataType<*>) -> Unit) : SkyblockEvent() {

    fun register(type: DataType<*>) {
        registry(type)
    }
}
