package tech.thatgravyboat.skyblockapi.utils.extentions

fun Int.roundToNextMultipleOf(multiple: Int) = (this + multiple - 1) / multiple * multiple
