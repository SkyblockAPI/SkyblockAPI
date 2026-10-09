package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.NamedCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class Loadout(
    var currentSlot: Int = -1,
    @NamedCodec("loadout_slots") var slots: MutableMap<Int, LoadoutSlot> = mutableMapOf(),
) {
    public companion object {
        @IncludedCodec(named = "loadout_slots")
        public val slotCodec: Codec<MutableMap<Int, LoadoutSlot>> = SkyblockAPICodecs.getCodec<LoadoutSlot>().listOf().xmap(
            { it.associateByTo(mutableMapOf(), LoadoutSlot::id) },
            { it.values.toList() }
        )

        public val CODEC: Codec<Loadout> = SkyblockAPICodecs.getCodec<Loadout>()
    }
}
