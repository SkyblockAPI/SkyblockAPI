package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes.ID
import tech.thatgravyboat.skyblockapi.utils.extentions.getIntOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.getStringOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.tag

/**
 * Data types for things like personal compactor and deletor
 */
@Module
object PersonalAccessoryDataTypes {

    private fun ItemStack.getMaxItems(type: String) = when (ID.factory(this)) {
        "PERSONAL_${type}_7000" -> 12
        "PERSONAL_${type}_6000" -> 7
        "PERSONAL_${type}_5000" -> 3
        "PERSONAL_${type}_4000" -> 1
        else -> null
    }

    val PERSONAL_COMPACTOR_ITEMS: DataType<List<String?>> = DataType.of("personal_compactor") {
        val maxItems = it.getMaxItems("COMPACTOR") ?: return@of null
        buildList {
            for (i in 0 until maxItems) {
                add(it.tag?.getStringOrNull("personal_compact_$i"))
            }
        }
    }

    val PERSONAL_DELETOR_ITEMS: DataType<List<String?>> = DataType.of("personal_deletor") {
        val maxItems = it.getMaxItems("DELETOR") ?: return@of null
        buildList {
            for (i in 0 until maxItems) {
                add(it.tag?.getStringOrNull("personal_deletor_$i"))
            }
        }
    }

    val PERSONAL_ACCESSORY_ACTIVE: DataType<Boolean> = DataType.of("personal_accessory_active") {
        it.tag?.getIntOrNull("PERSONAL_DELETOR_ACTIVE")?.let { active -> active == 1 }
    }

}
