package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getTag

@Module
object GenericDataTypes {

    val ID: DataType<String> = DataType("id") { it.getTag("id")?.asString }
    val MODIFIER: DataType<String> = DataType("modifier") { it.getTag("modifier")?.asString }

    @Subscription
    fun onDataTypeRegistration(event: RegisterDataTypesEvent) {
        event.register(ID)
        event.register(MODIFIER)
    }
}
