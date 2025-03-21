package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import kotlinx.datetime.Instant
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NumericTag
import net.minecraft.nbt.Tag
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.FishingRodPart
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getTag
import java.util.*

@Module
object GenericDataTypes {

    val ID: DataType<String> = DataType("id") { it.getTag("id")?.asString }
    val UUID: DataType<UUID> = DataType("uuid") { runCatching { it.getTag("uuid")?.asString?.let(java.util.UUID::fromString) }.getOrNull() }
    val MODIFIER: DataType<String> = DataType("modifier") { it.getTag("modifier")?.asString }
    val TIMESTAMP: DataType<Instant> = DataType("timestamp") { it.getTag("timestamp")?.asLong?.let(Instant::fromEpochMilliseconds) }
    val SECONDS_HELD: DataType<Int> = DataType("seconds_held") { it.getTag("seconds_held")?.asInt }
    val PICKONIMBUS_DURABILITY: DataType<Int> = DataType("pickonimbus_durability") { it.getTag("pickonimbus_durability")?.asInt }
    val RARITY_UPGRADES: DataType<Int> = DataType("rarity_upgrades") { it.getTag("rarity_upgrades")?.asInt }
    val QUIVER_ARROW: DataType<Boolean> = DataType("quiver_arrow") { it.getTag("quiver_arrow")?.asString?.equals("true") }
    val ENCHANTMENTS: DataType<Map<String, Int>> = DataType("enchantments") {
        it.getTag("enchantments")?.asObject?.let { tag ->
            buildMap { tag.allKeys.forEach { key -> this[key] = tag.getInt(key) } }
        }
    }
    val POTION: DataType<String> = DataType("potion") { it.getTag("potion")?.asString }
    val POTION_LEVEL: DataType<Int> = DataType("potion_level") { it.getTag("potion_level")?.asInt }
    val ATTRIBUTES: DataType<Map<String, Int>> = DataType("attributes") {
        it.getTag("attributes")?.asObject?.let { tag ->
            buildMap { tag.allKeys.forEach { key -> this[key] = tag.getInt(key) } }
        }
    }

    val HOOK: DataType<FishingRodPart> = getFishingRodPartDataType("hook")
    val LINE: DataType<FishingRodPart> = getFishingRodPartDataType("line")
    val SINKER: DataType<FishingRodPart> = getFishingRodPartDataType("sinker")

    @Subscription
    fun onDataTypeRegistration(event: RegisterDataTypesEvent) {
        event.register(ID)
        event.register(UUID)
        event.register(MODIFIER)
        event.register(TIMESTAMP)
        event.register(SECONDS_HELD)
        event.register(PICKONIMBUS_DURABILITY)
        event.register(RARITY_UPGRADES)
        event.register(QUIVER_ARROW)
        event.register(ENCHANTMENTS)
        event.register(POTION)
        event.register(POTION_LEVEL)
        event.register(ATTRIBUTES)  
        event.register(HOOK)
        event.register(LINE)
        event.register(SINKER)
    }

    private val Tag.asInt get() = (this as? NumericTag)?.asInt
    private val Tag.asLong get() = (this as? NumericTag)?.asLong
    private val Tag.asObject get() = (this as? CompoundTag)

    private fun getFishingRodPartDataType(name: String) = DataType(name) {
        val tag = it.getTag(name)?.asObject ?: return@DataType null
        FishingRodPart(java.util.UUID.fromString(tag.getString("uuid")), tag.getString("part"))
    }
}
