package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import kotlinx.datetime.Instant
import net.minecraft.Util
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import java.util.*

@Module
object GenericDataTypes {

    val ID: DataType<String> = DataType("id") { it.tag?.getStringOrNull("id") }
    val UUID: DataType<UUID> = DataType("uuid") { it.tag?.getUuidOrNull("uuid") }
    val MODIFIER: DataType<String> = DataType("modifier") { it.tag?.getStringOrNull("modifier") }
    val TIMESTAMP: DataType<Instant> = DataType("timestamp") { it.tag?.getLongOrNull("timestamp")?.let(Instant::fromEpochMilliseconds) }
    val SECONDS_HELD: DataType<Int> = DataType("seconds_held") { it.tag?.getIntOrNull("seconds_held") }
    val PICKONIMBUS_DURABILITY: DataType<Int> = DataType("pickonimbus_durability") { it.tag?.getIntOrNull("pickonimbus_durability") }
    val RARITY_UPGRADES: DataType<Int> = DataType("rarity_upgrades") { it.tag?.getIntOrNull("rarity_upgrades") }
    val QUIVER_ARROW: DataType<Boolean> = DataType("quiver_arrow") { it.tag?.getStringOrNull("quiver_arrow")?.equals("true") }
    val ENCHANTMENTS: DataType<Map<String, Int>> = DataType("enchantments") {
        it.tag?.getCompoundOrEmpty("enchantments")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val HOT_POTATO_BOOKS: DataType<Int> = DataType("hot_potato_count") { it.tag?.getIntOrNull("hot_potato_count") }
    val GEMSTONES: DataType<List<GemstoneSlotData>> = DataType("gemstones") { it.tag?.let(::parseGemstones) }
    val POTION: DataType<String> = DataType("potion") { it.tag?.getStringOrNull("potion") }
    val POTION_LEVEL: DataType<Int> = DataType("potion_level") { it.tag?.getIntOrNull("potion_level") }
    val ATTRIBUTES: DataType<Map<String, Int>> = DataType("attributes") {
        it.tag?.getCompoundOrEmpty("attributes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val CROPS_BROKEN: DataType<Long> = DataType("mined_crops") { it.tag?.getLongOrNull("mined_crops") }
    val COMPACT_BLOCKS: DataType<Long> = DataType("compact_blocks") { it.tag?.getLongOrNull("compact_blocks") }

    val HOOK: DataType<Pair<UUID, String>> = getFishingRodPartDataType("hook")
    val LINE: DataType<Pair<UUID, String>> = getFishingRodPartDataType("line")
    val SINKER: DataType<Pair<UUID, String>> = getFishingRodPartDataType("sinker")

    val FUEL_TANK: DataType<String> = DataType("drill_part_fuel_tank") { it.tag?.getStringOrNull("drill_part_fuel_tank") }
    val ENGINE: DataType<String> = DataType("drill_part_engine") { it.tag?.getStringOrNull("drill_part_engine") }
    val UPGRADE_MODULE: DataType<String> = DataType("drill_part_upgrade_module") { it.tag?.getStringOrNull("drill_part_upgrade_module") }

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
        event.register(HOT_POTATO_BOOKS)
        event.register(GEMSTONES)
        event.register(POTION)
        event.register(POTION_LEVEL)
        event.register(ATTRIBUTES)
        event.register(CROPS_BROKEN)
        event.register(COMPACT_BLOCKS)
        event.register(HOOK)
        event.register(LINE)
        event.register(SINKER)
        event.register(FUEL_TANK)
        event.register(ENGINE)
        event.register(UPGRADE_MODULE)
    }

    private fun getFishingRodPartDataType(name: String) = DataType(name) {
        val tag = it.tag?.getObjectOrNull(name) ?: return@DataType null
        val uuid = tag.getUuidOrNull("uuid") ?: Util.NIL_UUID
        uuid to tag.getStringOr("part", "")
    }
}
