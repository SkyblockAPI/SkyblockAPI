package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.MaxwellData
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPower
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPowers
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuning
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuningTemplate
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.repo.accessory.RepoAccessoryData
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.OptionalAliasMapCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs.getCodec
import tech.thatgravyboat.skyblockapi.utils.extentions.associateByNotNull
import tech.thatgravyboat.skyblockapi.utils.extentions.filterValuesNotNull
import tech.thatgravyboat.skyblockapi.utils.extentions.isSameItem
import tech.thatgravyboat.skyblockapi.utils.extentions.replaceWith
import tech.thatgravyboat.skyblockapi.utils.extentions.withoutGetter
import tech.thatgravyboat.skyblockapi.utils.extentions.copyWithoutLinesAfterRarity
import kotlin.Boolean

internal object MaxwellStorage {

    //region Old Codecs
    private val CODEC_V0: MapCodec<MaxwellData> = CodecUtils.lazyMapCodec {
        RecordCodecBuilder.mapCodec {
            it.group(
                getCodec<MaxwellPower>().optionalFieldOf("power").withoutGetter(),
                OptionalAliasMapCodec(listOf("accessory_power", "magicalPower"), false, getCodec<Int>()).withoutGetter(),
                CodecUtils.mutableList(getCodec<ItemStack>()).optionalFieldOf("accessories").withoutGetter(),
                CodecUtils.mutableSet(getCodec<MaxwellPower>()).optionalFieldOf("unlockedPowers").withoutGetter(),
                CodecUtils.compactMutableSet(getCodec<MaxwellTuning>()).optionalFieldOf("tunings").withoutGetter(),
                CodecUtils.compactMutableList(getCodec<MaxwellTuningTemplate>()).optionalFieldOf("tuning_templates").withoutGetter(),
                getCodec<Boolean>().optionalFieldOf("consumed_rift_prism").withoutGetter(),
            ).apply(it) { p_power, p_accessoryPower, p_accessories, p_unlockedPowers, p_tunings, p_tuningTemplates, p_consumedRiftPrism ->
                var obj = MaxwellData()
                if (p_power.isPresent) obj = obj.copy(power = p_power.get())
                if (p_accessoryPower.isPresent) obj = obj.copy(accessoryPower = p_accessoryPower.get())
                if (p_accessories.isPresent) {
                    obj = obj.copy(accessories = p_accessories.get().associateByNotNull { item ->
                        item.getSkyBlockId()?.let(RepoAccessoryData::getFamilyFromItemId)?.name
                    }.filterValuesNotNull().toMutableMap())
                }
                if (p_unlockedPowers.isPresent) obj = obj.copy(unlockedPowers = p_unlockedPowers.get())
                if (p_tunings.isPresent) obj = obj.copy(tunings = p_tunings.get())
                if (p_tuningTemplates.isPresent) obj = obj.copy(tuningTemplates = p_tuningTemplates.get())
                if (p_consumedRiftPrism.isPresent) obj = obj.copy(consumedRiftPrism = p_consumedRiftPrism.get())
                obj
            }
        }
    }
    //endregion

    private val DATA = StoredProfileData<MaxwellData>(
        "maxwell",
        version = 1,
    ) { version ->
        when (version) {
            0 -> CODEC_V0.codec()
            else -> MaxwellData.CODEC
        }
    }

    private inline val data: MaxwellData? get() = DATA.get()

    val power: MaxwellPower
        get() = data?.power ?: MaxwellPowers.NO_POWER

    val accessoryPower: Int
        get() = data?.accessoryPower ?: 0

    val accessories: Map<String, ItemStack>
        get() = data?.accessories.orEmpty()

    val unlockedPowers: Set<MaxwellPower>
        get() = data?.unlockedPowers.orEmpty()

    val tunings: Set<MaxwellTuning>
        get() = data?.tunings.orEmpty()

    val tuningTemplates: List<MaxwellTuningTemplate>
        get() = data?.tuningTemplates.orEmpty()

    fun updatePower(newPower: MaxwellPower) {
        DATA.edit {
            if (power == newPower) return
            power = newPower
            addUnlockedPower(newPower)
        }
    }

    fun updateAccessoryPower(newAccessoryPower: Int) {
        DATA.edit {
            if (accessoryPower == newAccessoryPower) return
            accessoryPower = newAccessoryPower
        }
    }

    fun removeAccessory(family: String) {
        DATA.editIf {
            accessories.remove(family) != null
        }
    }

    fun updateAccessory(family: String, item: ItemStack) {
        DATA.edit {
            val prev = accessories[family]
            val newCleanItem = item.copyWithoutLinesAfterRarity()
            if (prev != null && prev.isSameItem(newCleanItem)) return
            accessories[family] = newCleanItem
        }
    }

    fun addUnlockedPower(power: MaxwellPower) {
        DATA.editIf {
            unlockedPowers.add(power)
        }
    }

    fun removeTuning(stat: SkyBlockStat) {
        DATA.editIf {
            tunings.removeIf { it.stat == stat }
        }
    }

    fun setTuning(tuning: MaxwellTuning) {
        DATA.edit {
            if (tuning in tunings) return
            tunings.removeIf { it.stat == tuning.stat }
            tunings.add(tuning)
        }
    }

    fun updateTunings(newTunings: Collection<MaxwellTuning>) {
        DATA.edit {
            if (tunings == newTunings) return
            this.tunings.replaceWith(newTunings)
        }
    }

    fun updateTuningTemplateSlot(index: Int, tunings: Set<MaxwellTuning>) {
        DATA.edit {
            val current = tuningTemplates.find { it.index == index }

            if (current == null) {
                tuningTemplates.add(MaxwellTuningTemplate(index, locked = false, tunings))
                return@edit
            }

            if (!current.locked && current.tunings == tunings) return
            current.locked = false
            current.tunings = tunings
        }
    }

    fun lockTuningTemplateSlot(index: Int) {
        DATA.edit {
            val current = tuningTemplates.find { it.index == index }

            if (current == null) {
                tuningTemplates.add(MaxwellTuningTemplate(index))
                return@edit
            }

            if (current.locked && current.tunings.isEmpty()) return
            current.locked = true
            current.tunings = emptySet()
        }
    }

    // if we store an accessory family whose name gets changed later on, we fix it here
    fun fixAccessoryFamilies() {
        DATA.edit {
            val it = accessories.iterator()
            val toAdd = mutableMapOf<String, ItemStack>()
            while (it.hasNext()) {
                val (storedFamilyName, item) = it.next()
                val id = item.getSkyBlockId() ?: continue
                val family = RepoAccessoryData.getFamilyFromItemId(id) ?: continue
                if (family.name == storedFamilyName) continue
                it.remove()
                toAdd[family.name] = item
            }
            if (toAdd.isEmpty()) return
            toAdd.forEach { (familyName, itemStack) -> accessories[familyName] = itemStack }
        }
    }

    fun reset() = DATA.deleteCurrent()
}
