package tech.thatgravyboat.skyblockapi.api.data.stored

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.CrystalStatus
import tech.thatgravyboat.skyblockapi.api.data.CrystalType
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.profile.hotm.CrystalData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix

@Module
internal object CrystalStorage {
    private val CRYSTAL_DATA = StoredProfileData(
        ::CrystalData,
        CrystalData.CODEC,
        "crystal_data.json",
    )

    val crystalData: MutableMap<CrystalType, CrystalStatus> get() = CRYSTAL_DATA.get()?.crystals ?: mutableMapOf()

    fun setCrystalStatusByName(typeName: String, status: CrystalStatus): Boolean {
        val type = CrystalType.entries.find { it.name.equals(typeName, true) } ?: return false
        crystalData[type] = status
        save()
        return true
    }

    fun save() {
        CRYSTAL_DATA.save()
    }

    @Subscription
    fun onCommandRegister(event: RegisterCommandsEvent) {
        event.register("meowmrrow") {
            callback {
                val statuses = crystalData.entries.joinToString(", ") { "${it.key.name}: ${it.value}" }
                Text.of(statuses).sendWithPrefix()
            }
        }
    }
}
