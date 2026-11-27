package tech.thatgravyboat.skyblockapi.api.profile.items.compactor

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.RegisterableValues
import tech.thatgravyboat.skyblockapi.utils.devutils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.toSnakeCase
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase

public data class PersonalCompactorType internal constructor(
    val name: String,
    val internalName: String,
    val skyblockId: SkyBlockId,
) {
    val items: Map<SkyBlockId, Boolean>
        get() = PersonalCompactorAPI.compactors[this].orEmpty()
    val sackThreshold: Boolean
        get() = PersonalCompactorAPI.hasSackThreshold(this)


    public companion object {

        @IncludedCodec(keyable = true)
        public val CODEC: Codec<PersonalCompactorType> = Codec.STRING.xmap(
            PersonalCompactorTypes::getByInternalName,
            PersonalCompactorType::internalName
        )

    }

}

@Module
public object PersonalCompactorTypes : RegisterableValues<PersonalCompactorType>() {
    override val name: String = "personal_compactor"
    override val debugToggle: DebugToggle = debugToggle("unknown.personal_compactor", "Warns when detecting a new unknown personal compactor type")

    public fun getByNameOrNull(name: String): PersonalCompactorType? = registered.values.find { it.name.equals(name, true) }
    public fun getByName(name: String): PersonalCompactorType = getByNameOrNull(name) ?: register(name.toSnakeCase(), name)

    public fun getByInternalNameOrNull(id: String): PersonalCompactorType? = registered[id]
    public fun getByInternalName(id: String): PersonalCompactorType = getByInternalNameOrNull(id) ?: register(id, id.toTitleCase())

    private fun register(id: String, name: String = id.toTitleCase(), skyblockId: String = "personal_compactor_${id}_4000"): PersonalCompactorType {
        return getOrCreate(id.lowercase()) { key ->
            PersonalCompactorType(name, key, SkyBlockId.item(skyblockId))
        }
    }

    public val FARMING: PersonalCompactorType = register("farming")
    public val MINING: PersonalCompactorType = register("foraging")
    public val COMBAT: PersonalCompactorType = register("combat")
    public val FORAGING: PersonalCompactorType = register("foraging")
    public val FISHING: PersonalCompactorType = register("fishing")
    public val HUSBANDRY: PersonalCompactorType = register("husbandry")
}
