package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotm.HotmData
import tech.thatgravyboat.skyblockapi.api.profile.hotm.HotmPerk

internal object HotmStorage : HotxStorage<HotmData, HotmPerk>() {

    override var STORAGE = StoredProfileData(
        ::HotmData,
        HotmData.CODEC,
        "hotm.json",
    )

}
