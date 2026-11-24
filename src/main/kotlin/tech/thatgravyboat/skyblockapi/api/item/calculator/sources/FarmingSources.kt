package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.item.calculator.IntDataTypeWithLimitCalculator

internal object OverclockerCalculator : IntDataTypeWithLimitCalculator(DataTypes.TOOL_OVERCLOCKS, "OVERCLOCKER_3000", 10)

internal object FarmingForDummiesCalculator : IntDataTypeWithLimitCalculator(DataTypes.FARMING_FOR_DUMMIES, "FARMING_FOR_DUMMIES", 5)
