package tech.thatgravyboat.skyblockapi.utils.extentions

public fun Int.roundToNextMultipleOf(multiple: Int): Int = (this + multiple - 1) / multiple * multiple
