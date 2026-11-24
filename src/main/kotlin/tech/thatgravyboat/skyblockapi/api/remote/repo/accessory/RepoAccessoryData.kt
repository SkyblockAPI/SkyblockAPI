package tech.thatgravyboat.skyblockapi.api.remote.repo.accessory

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

// TODO: change this to use remote repo when we have that system
@Module
public object RepoAccessoryData {
    // the temporary accessories file is copy-pasted from meowdding repo, and so the alternative codec is needed
    private val CODEC = Codec.withAlternative(
        SkyblockAPICodecs.getCodec<AccessoryData>(),
        AccessoryFamily.CODEC.listOf().xmap(::AccessoryData, AccessoryData::families)
    )
    internal val data = SkyBlockAPI.getRepo("accessories", CODEC)

    public val families: Map<String, AccessoryFamily> = data.families.associateBy { it.name }

    public fun getFamilyFromItemName(name: String): AccessoryFamily? {
        val id = SimpleItemAPI.findIdByName(name) ?: return null
        return getFamilyFromItemId(id)
    }

    public fun getFamilyFromItemId(id: SkyBlockId): AccessoryFamily? {
        return families.values.find { it.contains(id) }
    }

    @GenerateCodec
    internal data class AccessoryData(
        val families: List<AccessoryFamily>,
    )

}
