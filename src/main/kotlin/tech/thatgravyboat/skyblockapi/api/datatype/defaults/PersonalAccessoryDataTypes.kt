package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.utils.extentions.asReversedIterator
import tech.thatgravyboat.skyblockapi.utils.extentions.getIntOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.unsafeTag
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import kotlin.jvm.optionals.getOrNull

/**
 * Data types for things like personal compactor and deletor
 */
@Module
public object PersonalAccessoryDataTypes {

    private fun ResolutionContext.isPersonalAccessory(type: String): Boolean {
        return this[ResolutionContext.Resolver.ID]?.startsWith("PERSONAL_${type}") == true
    }

    private fun ResolutionContext.isCompactor(): Boolean = isPersonalAccessory("COMPACTOR")
    private fun ResolutionContext.isDeletor(): Boolean = isPersonalAccessory("DELETOR")

    private val group = LoreDataTypes.dataTypeGroup.group("personal_accessory")
    private val personalAccessoryActiveRegex = group.create(
        "enabled",
        "Enabled: (?<state>On|Off)",
    )
    private val personalAccessoryToggleRegex = group.create(
        "toggle",
        "Right-click to toggle!"
    )

    private const val PERSONAL_DELETE = "personal_deletor"
    private const val PERSONAL_COMPACT = "personal_compact"

    public val PERSONAL_COMPACTOR_ITEMS: DataType<List<String?>> = DataType.of("personal_compactor") { ctx, item ->
        if (!ctx.isCompactor()) return@of null
        buildList {
            fun setIndex(index: Int, string: String) {
                if (index < 0) return
                while (index !in indices) add(null)
                if (index in indices) this[index] = string
            }

            item.unsafeTag?.forEach { key, tag ->
                if (!key.startsWith(PERSONAL_COMPACT)) return@forEach
                val index = key.substring(PERSONAL_COMPACT.length).toIntOrNull() ?: return@forEach
                val string = tag.asString().getOrNull() ?: return@forEach
                setIndex(index, string)
            }
        }
    }

    public val PERSONAL_DELETOR_ITEMS: DataType<List<String?>> = DataType.of("personal_deletor") { ctx, item ->
        if (!ctx.isDeletor()) return@of null
        buildList {
            fun setIndex(index: Int, string: String) {
                if (index < 0) return
                while (index !in indices) add(null)
                if (index in indices) this[index] = string
            }

            item.unsafeTag?.forEach { key, tag ->
                if (!key.startsWith(PERSONAL_DELETE)) return@forEach
                val index = key.substring(PERSONAL_DELETE.length).toIntOrNull() ?: return@forEach
                val string = tag.asString().getOrNull() ?: return@forEach
                setIndex(index, string)
            }
        }
    }

    public val PERSONAL_ACCESSORY_ACTIVE: DataType<Boolean> = DataType.of("personal_accessory_active") { ctx, stack ->
        if (!ctx.isCompactor() && !ctx.isDeletor()) return@of null
        val fromTag = stack.unsafeTag?.getIntOrNull("PERSONAL_DELETOR_ACTIVE")
        if (fromTag != null) {
            return@of fromTag == 1
        }

        val lore = ctx[ResolutionContext.Resolver.RAW_LORE]

        var checkNextLine = false
        for (line in lore.asReversedIterator()) {
            if (checkNextLine) {
                val state = personalAccessoryActiveRegex.findGroup(line, "state")
                return@of when (state) {
                    "On" -> true
                    "Off" -> false
                    else -> null
                }
            }
            if (personalAccessoryToggleRegex.contains(line)) checkNextLine = true
        }
        return@of null

    }

}
