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
        it.getTag("timestamp")?.asLong?.let(Instant::fromEpochMilliseconds)
    }
    val SECONDS_HELD: DataType<Int> = DataType("seconds_held") { it.getTag("seconds_held")?.asInt }
    val PICKONIMBUS_DURABILITY: DataType<Int> = DataType("pickonimbus_durability") { it.getTag("pickonimbus_durability")?.asInt }
    val RARITY_UPGRADES: DataType<Int> = DataType("rarity_upgrades") { it.getTag("rarity_upgrades")?.asInt }
    val QUIVER_ARROW: DataType<Boolean> = DataType("quiver_arrow") { it.getTag("quiver_arrow")?.asString?.equals("true") }

    @Subscription
    fun onDataTypeRegistration(event: RegisterDataTypesEvent) {
        event.register(ID)
        event.register(MODIFIER)
        event.register(TIMESTAMP)
        event.register(SECONDS_HELD)
        event.register(PICKONIMBUS_DURABILITY)
        event.register(RARITY_UPGRADES)
        event.register(QUIVER_ARROW)
    }

    private val Tag.asInt get() = (this as? NumericTag)?.asInt
    private val Tag.asLong get() = (this as? NumericTag)?.asLong

    // 10/04/24 m192AR
    //
    //  Rift Dimension
    //  ф Stillgore Château
    //
    // Motes: 196,935
    // Effigies: ⧯⧯⧯⧯⧯⧯
    //
    // www.hypixel.net

    //
}
