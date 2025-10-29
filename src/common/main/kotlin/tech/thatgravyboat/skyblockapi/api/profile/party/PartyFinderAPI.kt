package tech.thatgravyboat.skyblockapi.api.profile.party

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.area.isle.kuudra.KuudraTier
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.party.DungeonPartyFinderQueueEvent
import tech.thatgravyboat.skyblockapi.api.events.party.KuudraPartyFinderQueueEvent
import tech.thatgravyboat.skyblockapi.api.events.party.PartyFinderLeaveQueueEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen
import kotlin.text.get

@Module
object PartyFinderAPI {
    private val itemGroup = RegexGroup.Companion.INVENTORY.group("partyfinder")
    private val chatGroup = RegexGroup.Companion.CHAT.group("partyfinder")

    private val dungeonTypeItemRegex = itemGroup.create(
        "dungeon_type_item",
        "Select Dungeon Type"
    )
    private val dungeonTypeRegex = itemGroup.create(
        "dungeon_type",
        "Currently Selected: (?<type>[\\w ]+)"
    )

    private val kuudraTierItemRegex = itemGroup.create(
        "kuudra_tier_item",
        "Select Tier"
    )
    private val kuudraTierRegex = itemGroup.create(
        "kuudra_tier",
        "Currently Selected: (?<tier>[\\w ]+)"
    )

    private val dungeonFloorItemRegex = itemGroup.create(
        "dungeon_floor_item",
        "Select Floor"
    )
    private val dungeonFloorRegex = itemGroup.create(
        "dungeon_floor",
        "Currently Selected: (?<floor>[\\w ]+)"
    )

    private val groupNoteItemRegex = itemGroup.create(
        "group_note_item",
        "Set Group Note"
    )
    private val groupNotePrefixRegex = itemGroup.create(
        "group_note_prefix",
        "Current Note:"
    )

    private val classRequiredItemRegex = itemGroup.create(
        "class_required_item",
        "Set Class Level Required"
    )
    private val classRequiredPrefixRegex = itemGroup.create(
        "class_required_prefix",
        "Current Level Required:"
    )

    private val dungeonRequiredItemRegex = itemGroup.create(
        "dungeon_required_item",
        "Set Dungeon Level Required"
    )
    private val dungeonRequiredPrefixRegex = itemGroup.create(
        "dungeon_required_prefix",
        "Current Level Required:"
    )

    private val combatRequiredItemRegex = itemGroup.create(
        "combat_required_item",
        "Set Combat Level Required"
    )
    private val combatRequiredPrefixRegex = itemGroup.create(
        "combat_required_prefix",
        "Current Level Required:"
    )

    private val partyFinderQueueRegex = chatGroup.create(
        "start_dungeon_queue",
        "Party Finder > Your party has been queued in the dungeon finder!"
    )
    private val kuudraPartyFinderQueueRegex = chatGroup.create(
        "start_kuudra_queue",
        "Party Finder > Your party has been queued in the party finder!"
    )
    private val partyFinderDelistRegex = chatGroup.create(
        "stop_queue",
        "Party Finder > Your group has been de-listed!"
    )
    private val partyFinderRemovedRegex = chatGroup.create(
        "remove_queue",
        "Party Finder > Your group has been removed from the party finder because the leader went offline!"
    )

    var queuedKuudraTier: KuudraTier? = null
        private set
    var queuedDungeonFloor: DungeonFloor? = null
        private set
    var groupNote: String = ""
        private set
    var classLevelRequirement: Int = 0
        private set
    var dungeonLevelRequirement: Int = 0
        private set
    var combatLevelRequirement: Int = 0
        private set


    private var dungeonType: String? = null

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (event.title != "Group Builder") return
        if (event.isInPlayerInventory) return
        if (!event.isInMainPart) return
        if (event.isSkyBlockFiller) return

        matchWhen(event.item.cleanName) {
            case(kuudraTierItemRegex) {
                event.item.getLore().forEach { component ->
                    val match = kuudraTierRegex.matchEntire(component.string) ?: return@forEach
                    val tier = match.groups["tier"]!!.value

                    queuedKuudraTier = KuudraTier.getByName(tier.removeSuffix(" Tier"))
                }
            }
            case(dungeonTypeItemRegex) {
                event.item.getLore().forEach { component ->
                    val match = dungeonTypeRegex.matchEntire(component.string) ?: return@forEach

                    dungeonType = match.groups["type"]!!.value
                }
            }
            case(dungeonFloorItemRegex) {
                event.item.getLore().forEach { component ->
                    val match = dungeonFloorRegex.matchEntire(component.string) ?: return@forEach

                    val floor = match.groups["floor"]!!.value

                    queuedDungeonFloor = DungeonFloor.Companion.getByLongName("$dungeonType $floor")
                }
            }
            case(groupNoteItemRegex) {
                event.item.getLore().forEachIndexed { index, component ->
                    if (!groupNotePrefixRegex.matches(component.string)) return@forEachIndexed
                    val note = event.item.getLore().getOrNull(index + 1)?.string ?: return@forEachIndexed
                    groupNote = note.removeSuffix(".")
                    return@case
                }
            }
            case(classRequiredItemRegex) {
                event.item.getLore().forEachIndexed { index, component ->
                    if (!classRequiredPrefixRegex.matches(component.string)) return@forEachIndexed
                    val note = event.item.getLore().getOrNull(index + 1)?.string ?: return@forEachIndexed
                    classLevelRequirement = note.removeSuffix(".").toIntOrNull() ?: 0
                    return@case
                }
            }
            case(dungeonRequiredItemRegex) {
                event.item.getLore().forEachIndexed { index, component ->
                    if (!dungeonRequiredPrefixRegex.matches(component.string)) return@forEachIndexed
                    val note = event.item.getLore().getOrNull(index + 1)?.string ?: return@forEachIndexed
                    dungeonLevelRequirement = note.removeSuffix(".").toIntOrNull() ?: 0
                    return@case
                }
            }
            case(combatRequiredItemRegex) {
                event.item.getLore().forEachIndexed { index, component ->
                    if (!combatRequiredPrefixRegex.matches(component.string)) return@forEachIndexed
                    val note = event.item.getLore().getOrNull(index + 1)?.string ?: return@forEachIndexed
                    combatLevelRequirement = note.removeSuffix(".").toIntOrNull() ?: 0
                    return@case
                }
            }
        }
    }

    @Subscription
    fun onChatMessage(event: ChatReceivedEvent.Pre) {
        matchWhen(event.text) {
            case(partyFinderQueueRegex) {
                DungeonPartyFinderQueueEvent(
                    queuedDungeonFloor ?: return@case,
                    groupNote,
                    dungeonLevelRequirement,
                    classLevelRequirement
                ).post()
            }

            case (kuudraPartyFinderQueueRegex) {
                KuudraPartyFinderQueueEvent(
                    queuedKuudraTier ?: return@case,
                    groupNote,
                    combatLevelRequirement
                ).post()
            }

            case(partyFinderDelistRegex) {
                PartyFinderLeaveQueueEvent.post()
            }
            case(partyFinderRemovedRegex) {
                PartyFinderLeaveQueueEvent.post()
            }
        }

    }
}
