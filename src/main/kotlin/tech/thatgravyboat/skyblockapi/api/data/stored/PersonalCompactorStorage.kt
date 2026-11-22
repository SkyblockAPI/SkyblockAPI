package tech.thatgravyboat.skyblockapi.api.data.stored

import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.compactor.PersonalCompactorType
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.changeValue

@Module
internal object PersonalCompactorStorage {
    private val STORAGE = StoredProfileData<PersonalCompactorData>("personal_compactor")
    private inline val data: PersonalCompactorData?
        get() = STORAGE.get()

    val compactors: Map<PersonalCompactorType, Map<SkyBlockId, Boolean>>
        get() = data?.items.orEmpty()
    val sackThreshold: Map<PersonalCompactorType, Boolean>
        get() = data?.sackThreshold.orEmpty()

    fun setItemStatus(type: PersonalCompactorType, id: SkyBlockId, status: Boolean) {
        STORAGE.edit {
            items.getOrPut(type, ::mutableMapOf).changeValue(id, status)
        }
    }

    fun setSackThreshold(type: PersonalCompactorType, value: Boolean) {
        STORAGE.edit {
            sackThreshold.changeValue(type, value)
        }
    }

}

@GenerateCodec
internal data class PersonalCompactorData(
    @FieldName("sack_threshold")
    var sackThreshold: MutableMap<PersonalCompactorType, Boolean> = mutableMapOf(),
    val items: MutableMap<PersonalCompactorType, MutableMap<SkyBlockId, Boolean>> = mutableMapOf()
)


