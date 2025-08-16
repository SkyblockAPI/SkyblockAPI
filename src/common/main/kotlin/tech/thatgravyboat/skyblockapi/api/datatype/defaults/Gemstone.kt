package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import kotlin.jvm.optionals.getOrNull

enum class Gemstone {
    RUBY,

    AMBER,
    TOPAZ,
    JADE,
    SAPPHIRE,
    AMETHYST,

    JASPER,
    OPAL,

    AQUAMARINE,
    CITRINE,
    ONYX,
    PERIDOT,
    ;
}

enum class GemstoneQuality {
    ROUGH,
    FLAWED,
    FINE,
    FLAWLESS,
    PERFECT,
    ;
}

enum class GemstoneSlot(vararg val gemstones: Gemstone) {
    JADE(Gemstone.JADE), // exists
    AMBER(Gemstone.AMBER), // exists
    TOPAZ(Gemstone.TOPAZ),
    SAPPHIRE(Gemstone.SAPPHIRE),
    AMETHYST(Gemstone.AMETHYST),
    JASPER(Gemstone.JASPER),
    OPAL(Gemstone.OPAL),
    RUBY(Gemstone.RUBY),
    CITRINE(Gemstone.CITRINE),
    AQUAMARINE(Gemstone.AQUAMARINE),
    PERIDOT(Gemstone.PERIDOT),
    ONYX(Gemstone.ONYX),
    CHISEL(Gemstone.CITRINE, Gemstone.AQUAMARINE, Gemstone.ONYX, Gemstone.PERIDOT),
    COMBAT(Gemstone.RUBY, Gemstone.AMETHYST, Gemstone.SAPPHIRE, Gemstone.JASPER, Gemstone.ONYX, Gemstone.OPAL),
    DEFENSIVE(Gemstone.AMETHYST, Gemstone.RUBY, Gemstone.OPAL),
    MINING(Gemstone.JADE, Gemstone.AMBER, Gemstone.TOPAZ), // exists
    UNIVERSAL(*Gemstone.entries.toTypedArray()),
    OFFENSIVE(Gemstone.SAPPHIRE, Gemstone.JASPER) // apparently unused
}

data class GemstoneSlotData(val gemstone: Gemstone, val slot: GemstoneSlot, val quality: GemstoneQuality) {
    val itemId = "${quality.name}_${gemstone.name}_GEM"
    val skyblockId = SkyBlockId.item(itemId)
}

fun parseGemstones(tag: CompoundTag?): List<GemstoneSlotData>? {
    tag ?: return null
    val gems = tag.getCompoundOrEmpty("gems") ?: return null

    return gems.keySet().mapNotNull { key ->
        GemstoneSlot.entries.find { slot ->
            key.substringBeforeLast("_").equals(slot.name, ignoreCase = true)
        }?.let { it to key }
    }.map { (slot, key) ->
        if (slot.gemstones.size == 1) {
            return@map GemstoneSlotData(slot.gemstones.first(), slot, gems.get(key).getQuality())
        }

        return@map GemstoneSlotData(Gemstone.valueOf(gems.get("${key}_gem")?.asString()?.getOrNull() ?: "RUBY"), slot, gems.get(key).getQuality())
    }
}

private fun Tag?.getQuality(): GemstoneQuality {
    return GemstoneQuality.valueOf(
        this?.let {
            val compound = it.asCompound()
            if (compound.isPresent) {
                return@let compound.get().get("quality")?.asString()?.orElse(null)
            }
            return@let it.asString().orElse(null)
        } ?: "ROUGH",
    )
}
