package tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft

import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

public enum class CorpseType(public val keyId: SkyBlockId? = null) {
    LAPIS,
    TUNGSTEN(SkyBlockId.item("tungsten_key")),
    UMBER(SkyBlockId.item("umber_key")),
    VANGUARD(SkyBlockId.item("skeleton_key")),
    ;

    //? if < 26.4 {
    /*@Deprecated("Use keyId instead", ReplaceWith("keyId"))
    public val key: String? = keyId?.skyblockId*///?}

    private val string = toFormattedName()
    override fun toString(): String = string

    public companion object {
        public fun byName(name: String): CorpseType? = entries.find { it.name.equals(name, true) }
    }
}
