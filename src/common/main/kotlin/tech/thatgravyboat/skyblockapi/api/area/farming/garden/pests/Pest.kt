package tech.thatgravyboat.skyblockapi.api.area.farming.garden.pests

import tech.thatgravyboat.skyblockapi.api.area.farming.garden.Crop
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class Pest(val spray: Spray? = null, val vinyl: Vinyl? = null, val crop: Crop? = null) {
    BEETLE(Spray.DUNG, Vinyl.BEETLE, Crop.NETHER_WART),
    CRICKET(Spray.HONEY_JAR, Vinyl.CRICKET_CHOIR, Crop.CARROT),
    FLY(Spray.DUNG, Vinyl.PRETTY_FLY, Crop.WHEAT),
    LOCUST(Spray.PLANT_MATTER, Vinyl.CICADA_SYMPHONY, Crop.POTATO),
    MITE(Spray.CHEESE_FUEL, Vinyl.DYNAMITES, Crop.CACTUS),
    MOSQUITO(Spray.COMPOST, Vinyl.BUZZIN_BEATS, Crop.SUGAR_CANE),
    MOTH(Spray.HONEY_JAR, Vinyl.WINGS_OF_HARMONY, Crop.COCOA_BEANS),
    RAT(Spray.CHEESE_FUEL, Vinyl.RODENT_REVOLUTION, Crop.PUMPKIN),
    SLUG(Spray.PLANT_MATTER, Vinyl.SLOW_AND_GROOVY, Crop.MUSHROOM),
    EARTHWORM(Spray.COMPOST, Vinyl.EARTHWORM_ENSEMBLE, Crop.MELON),
    FIELD_MOUSE,
    ;

    val displayName = toFormattedName()

    companion object {
        fun getPests(spray: Spray): List<Pest> = entries.filter { it.spray == spray }
    }
}
