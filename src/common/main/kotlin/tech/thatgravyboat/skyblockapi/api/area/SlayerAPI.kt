package tech.thatgravyboat.skyblockapi.api.area

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI as NewSlayerAPI
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType as NewSlayerType

@RemoveNextVersion(
    replaceWith = ReplaceWith("SlayerType", "tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI"),
)
object SlayerAPI {
    val type: SlayerType? get() = NewSlayerAPI.type?.let { SlayerType.fromNewType(it) }
    val level: Int get() = NewSlayerAPI.level
    val text: String? get() = NewSlayerAPI.text
    val current: Int get() = NewSlayerAPI.current
    val max: Int get() = NewSlayerAPI.max
}

@RemoveNextVersion(
    replaceWith = ReplaceWith("SlayerType", "tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType"),
)
enum class SlayerType(val displayName: String) {
    REVENANT_HORROR("Revenant Horror"),
    TARANTULA_BROODFATHER("Tarantula Broodfather"),
    SVEN_PACKMASTER("Sven Packmaster"),
    VOIDGLOOM_SERAPH("Voidgloom Seraph"),
    RIFTSTALKER_BLOODFIEND("Riftstalker Bloodfiend"),
    INFERNO_DEMONLORD("Inferno Demonlord"),
    ;

    companion object {
        fun fromDisplayName(displayName: String): SlayerType? = entries.firstOrNull {
            it.displayName.equals(displayName, ignoreCase = true)
        }

        internal fun fromNewType(slayerType: NewSlayerType): SlayerType {
            return when (slayerType) {
                NewSlayerType.REVENANT_HORROR -> REVENANT_HORROR
                NewSlayerType.TARANTULA_BROODFATHER -> TARANTULA_BROODFATHER
                NewSlayerType.SVEN_PACKMASTER -> SVEN_PACKMASTER
                NewSlayerType.VOIDGLOOM_SERAPH -> VOIDGLOOM_SERAPH
                NewSlayerType.RIFTSTALKER_BLOODFIEND -> RIFTSTALKER_BLOODFIEND
                NewSlayerType.INFERNO_DEMONLORD -> INFERNO_DEMONLORD
            }
        }
    }
}
