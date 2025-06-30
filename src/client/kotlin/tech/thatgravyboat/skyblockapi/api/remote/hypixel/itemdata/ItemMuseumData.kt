package tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata

import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.profile.items.museum.MuseumCategory

@GenerateCodec
data class ItemMuseumData(
    @FieldName("type") val category: MuseumCategory,
    @Compact @FieldName("armor_set") val armorSets: List<String> = emptyList(),
    @FieldName("parent") val parents: Map<String, String> = emptyMap()
)
