package tech.thatgravyboat.skyblockapi.api.area.farming.garden.pests

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class Spray(displayName: String? = null) {
    HONEY_JAR,
    DUNG,
    PLANT_MATTER,
    COMPOST,
    CHEESE_FUEL("Tasty Cheese"),
    ;

    val displayName: String = displayName ?: toFormattedName()
}
