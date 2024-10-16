package tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class MineshaftType(val id: String) {
    TOPAZ("TOPA"),
    SAPPHIRE("SAPP"),
    AMETHYST("AMET"),
    AMBER("AMBE"),
    JADE("JADE"),
    TITANIUM("TITA"),
    UMBER("UMBE"),
    TUNGSTEN("TUNG"),
    VANGUARD("FAIR"),
    RUBY("RUBY"),
    ONYX("ONYX"),
    AQUAMARINE("AQUA"),
    CITRINE("CITR"),
    PERIDOT("PERI"),
    JASPER("JASP"),
    OPAL("OPAL"),
    ;

    private val string = toFormattedName()
    override fun toString() = string

    companion object {
        fun fromId(id: String): MineshaftType? = entries.find { it.id.equals(id, true) }
    }
}
