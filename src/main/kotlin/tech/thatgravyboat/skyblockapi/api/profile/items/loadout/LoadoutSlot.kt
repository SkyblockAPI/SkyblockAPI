package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktcodecs.GenerateCodec
import java.util.UUID

@GenerateCodec
data class LoadoutSlot(
    val id: Int,
    var name: String?,
    var armor: Int?,
    var equipment: Int?,
    var pet: UUID?,
    var powerstone: String?,
    var tunings: List<String>?,
    var hotm: String?,
    var hotmSlot: Int?,
    var hotf: String?,
    var hotfSlot: Int?,
    var locked: Boolean,
)
