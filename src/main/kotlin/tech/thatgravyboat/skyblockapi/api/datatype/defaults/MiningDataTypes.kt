package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.utils.extentions.tag

@Module
internal object MiningDataTypes {

    val FUEL_TANK: DataType<String> = DataType.simple("drill_part_fuel_tank")
    val ENGINE: DataType<String> = DataType.simple("drill_part_engine")
    val UPGRADE_MODULE: DataType<String> = DataType.simple("drill_part_upgrade_module")

    val GEMSTONES: DataType<List<GemstoneSlotData>> = DataType.of("gemstones") { it.tag?.let(::parseGemstones) }

    val DIVAN_POWDER_COATING: DataType<Int> = DataType.simple("divan_powder_coating")
    val POLARVOID: DataType<Int> = DataType.simple("polarvoid")
    val POWER_ABILITY_SCROLL: DataType<String> = DataType.simple("power_ability_scroll")

    val COMPACT_BLOCKS: DataType<Long> = DataType.simple("compact_blocks")
    val PICKONIMBUS_DURABILITY: DataType<Int> = DataType.simple("pickonimbus_durability")

}
