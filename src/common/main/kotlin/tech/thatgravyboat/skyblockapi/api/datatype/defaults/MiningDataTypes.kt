package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.utils.extentions.getIntOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.getLongOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.getStringOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.tag

@Module
internal object MiningDataTypes {

    val FUEL_TANK: DataType<String> = DataType("drill_part_fuel_tank") { it.tag?.getStringOrNull("drill_part_fuel_tank") }
    val ENGINE: DataType<String> = DataType("drill_part_engine") { it.tag?.getStringOrNull("drill_part_engine") }
    val UPGRADE_MODULE: DataType<String> = DataType("drill_part_upgrade_module") { it.tag?.getStringOrNull("drill_part_upgrade_module") }

    val GEMSTONES: DataType<List<GemstoneSlotData>> = DataType("gemstones") { it.tag?.let(::parseGemstones) }

    val DIVAN_POWDER_COATING: DataType<Int> = DataType("divan_powder_coating") { it.tag?.getIntOrNull("divan_powder_coating") }
    val POLARVOID: DataType<Int> = DataType("polarvoid") { it.tag?.getIntOrNull("polarvoid") }
    val POWER_ABILITY_SCROLL: DataType<String> = DataType("power_ability_scroll") { it.tag?.getStringOrNull("power_ability_scroll") }

    val COMPACT_BLOCKS: DataType<Long> = DataType("compact_blocks") { it.tag?.getLongOrNull("compact_blocks") }
    val PICKONIMBUS_DURABILITY: DataType<Int> = DataType("pickonimbus_durability") { it.tag?.getIntOrNull("pickonimbus_durability") }

}
