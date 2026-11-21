package tech.thatgravyboat.skyblockapi.api.profile.items.compactor

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.PersonalCompactorStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.IgnoreFiller
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.command.MapBackedArgumentType
import tech.thatgravyboat.skyblockapi.utils.extentions.asReversedIterator
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.appendLine
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

// TODO: add pv loading?
@Module
public data object PersonalCompactorAPI : ItemDebugCategory {

    private val regexGroup = RegexGroup.INVENTORY.group("personal_compactor")

    private val titleRegex = regexGroup.create(
        "title",
        """(?:\(\d+/\d+\) )?Personal (?<type>\w+) Compactor"""
    )
    private val enabledRegex = regexGroup.create(
        "item.enabled",
        "^(?<status>ENABLED|DISABLED)$"
    )

    private val sackThresholdGroup = regexGroup.group("sack_threshold")

    private val sackThresholdNameRegex = sackThresholdGroup.create(
        "name",
        "Toggle 25% Threshold"
    )

    private val sackThresholdEnabledRegex = regexGroup.create(
        "enabled",
        "Currently: (?<status>ENABLED|DISABLED)"
    )


    public val compactors: Map<PersonalCompactorType, Map<SkyBlockId, Boolean>>
        get() = PersonalCompactorStorage.compactors

    public val sackThreshold: Map<PersonalCompactorType, Boolean>
        get() = PersonalCompactorStorage.sackThreshold

    public fun isCompacting(id: SkyBlockId): Boolean {
        return compactors.values.firstNotNullOfOrNull { items -> items[id] } ?: false
    }
    public fun hasSackThreshold(type: PersonalCompactorType): Boolean = sackThreshold[type] ?: true // it's enabled by default

    @Subscription
    @IgnoreFiller
    @OnlyOnSkyBlock
    private fun onInventoryChange(event: InventoryChangeEvent) {
        val typeString = titleRegex.findGroup(event.title, "type") ?: return
        val compactorType = PersonalCompactorTypes.getByName(typeString)

        if (event.isInMainPart) {
            val item = event.item
            val id = item.getSkyBlockId()?.asRoot() ?: return

            val status = enabledRegex.findGroup(item.getRawLore().asReversedIterator(), "status") ?: return
            val enabled = status == "ENABLED"

            PersonalCompactorStorage.setItemStatus(compactorType, id.asRoot(), enabled)
            event.addDebugString { "Compactor type: ${compactorType.internalName}" }
            event.addDebugString { "Id: $id" }
            event.addDebugString { "Enabled: $enabled" }
            return
        }

        if (sackThresholdNameRegex.matches(event.item.cleanName)) {
            val status = sackThresholdEnabledRegex.findGroup(event.item.getRawLore().asReversedIterator(), "status") ?: return
            val enabled = status == "ENABLED"
            PersonalCompactorStorage.setSackThreshold(compactorType, enabled)
            event.addDebugString { "Sack threshold: $enabled" }
        }

    }

    @Subscription
    private fun onRegisterSkyblockApiCommands(event: RegisterSkyblockApiCommandsEvent) {
        event.register("personal_compactor") {
            fun printType(type: PersonalCompactorType, printEmptyError: Boolean) {
                val map = compactors[type]?.ifEmpty { null } ?: run {
                    if (printEmptyError) {
                        Text.sendDebug("No data found for compactor type ") {
                            append(type.name, TextColor.AQUA)
                        }
                    }
                    return
                }
                Text.sendDebug("Personal ${type.name} Compactor: ") {
                    appendLine()
                    append(" - ", TextColor.GOLD)
                    append("Sack threshold: ", TextColor.BLUE)
                    val sackThreshold = type.sackThreshold
                    append(sackThreshold.toString(), if (sackThreshold) TextColor.GREEN else TextColor.RED)
                    appendLine()

                    map.entries.forEach { (id, state) ->
                        appendLine()
                        append(" - ", TextColor.GRAY)
                        append(id.cleanId, TextColor.AQUA)
                        append(": ", TextColor.GRAY)
                        append(state.toString(), if (state) TextColor.GREEN else TextColor.RED)
                    }
                }
            }
            callback {
                PersonalCompactorTypes.values.forEach { printType(it, false) }
            }
            thenCallback("type", MapBackedArgumentType(PersonalCompactorTypes.registered)) {
                val type = argument<PersonalCompactorType>("type")
                printType(type, true)
            }
        }
    }


}
