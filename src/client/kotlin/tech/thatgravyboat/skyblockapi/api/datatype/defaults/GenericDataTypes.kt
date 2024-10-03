package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import kotlinx.datetime.Instant
import net.minecraft.nbt.NumericTag
import net.minecraft.nbt.Tag
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getTag

@Module
object GenericDataTypes {

    val ID: DataType<String> = DataType("id") { it.getTag("id")?.asString }
    val MODIFIER: DataType<String> = DataType("modifier") { it.getTag("modifier")?.asString }
    val TIMESTAMP: DataType<Instant> = DataType("timestamp") {
        Instant.fromEpochMilliseconds(it.getTag("timestamp")?.asLong ?: 0)
    }
    val SECONDS_HELD: DataType<Int> = DataType("seconds_held") { it.getTag("seconds_held")?.asInt ?: 0 }
    val PICKONIMBUS_DURABILITY: DataType<Int> = DataType("pickonimbus_durability") { it.getTag("pickonimbus_durability")?.asInt ?: 0 }

    @Subscription
    fun onDataTypeRegistration(event: RegisterDataTypesEvent) {
        event.register(ID)
        event.register(MODIFIER)
        event.register(TIMESTAMP)
        event.register(SECONDS_HELD)
    }

    private val Tag.asInt get() = (this as? NumericTag)?.asInt
    private val Tag.asLong get() = (this as? NumericTag)?.asLong
}
