package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.NamedCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class Loadout(
    var currentSlot: Int = -1,
    @NamedCodec("loadout_slots") var slots: MutableMap<Int, LoadoutSlot> = mutableMapOf(),
) {
    companion object {
        @IncludedCodec(named = "loadout_slots")
        val slotCodec: Codec<MutableMap<Int, LoadoutSlot>> = SkyblockAPICodecs.getCodec<LoadoutSlot>().listOf().xmap(
            { it.associateByTo(mutableMapOf(), LoadoutSlot::id) },
            { it.values.toList() }
        )

        val CODEC = SkyblockAPICodecs.getCodec<Loadout>()
    }
}
