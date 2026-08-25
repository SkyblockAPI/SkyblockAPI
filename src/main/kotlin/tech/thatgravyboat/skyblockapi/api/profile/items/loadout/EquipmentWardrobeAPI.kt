package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.EquipmentStorage
import tech.thatgravyboat.skyblockapi.api.data.stored.LoadoutStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.equipment.EquipmentAPI
import tech.thatgravyboat.skyblockapi.api.profile.items.equipment.EquipmentSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutAPI.loadoutDebug
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.ColoredItems
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.SkyBlockApiDevUtils.debugString
import tech.thatgravyboat.skyblockapi.utils.extentions.roundToNextMultipleOf
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.math.max

private const val SELECT_START_INDEX = 36
private const val WARDROBE_SLOTS_PER_PAGE = 9

@Module
object EquipmentWardrobeAPI {
    private val wardrobeGroup = RegexGroup.INVENTORY.group("wardrobe.equipment")

    private val inventoryNameRegex = wardrobeGroup.create(
        "title",
        "\\((?<currentPage>\\d+)/\\d+\\) Equipment Sets",
    )

    private val equippedRegex = wardrobeGroup.create(
        "equip",
        "Slot \\d+: Equipped",
    )

    private val emptyEquipment = mutableListOf(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY)

    var inWardrobe = false
        private set

    /** 0 if not in wardrobe */
    var currentPage = 0
        private set

    val slots get() = LoadoutStorage.equipment?.slots ?: emptyList()
    val currentSlot: Int? get() = LoadoutStorage.equipment?.currentSlot
    var previousSlot: Int? = null

    val currentSet: Map<EquipmentSlot, ItemStack>
        get() {
            val slots = slots.find { it.id == currentSlot }?.slots ?: return emptyMap()
            return mapOf(
                EquipmentSlot.NECKLACE to slots[0],
                EquipmentSlot.CLOAK to slots[1],
                EquipmentSlot.BELT to slots[2],
                EquipmentSlot.GLOVES to slots[3],
            )
        }

    private fun processInventory(title: String, items: List<ItemStack>) {
        inventoryNameRegex.match(title, "currentPage") { (currentpage) ->
            currentpage.toIntOrNull()?.let { this.currentPage = it }
        }

        var foundCurrentSlot = false

        for (index in 0..<WARDROBE_SLOTS_PER_PAGE) {
            val selectStack = items[index + SELECT_START_INDEX]
            val id = WARDROBE_SLOTS_PER_PAGE * (currentPage - 1) + index + 1
            var locked = false

            if (selectStack.item == ColoredItems.RED_DYE) {
                locked = true
            } else if (equippedRegex.match(selectStack.hoverName.stripped)) {
                previousSlot = currentSlot
                LoadoutStorage.updateCurrentEquipmentSlot(id)
                EquipmentStorage.setEquipment(LoadoutStorage.equipment?.slots[id - 1])
                foundCurrentSlot = true
            }

            val slot1 = items[index].takeOrEmpty()
            val slot2 = items[index + 9].takeOrEmpty()
            val slot3 = items[index + 18].takeOrEmpty()
            val slot4 = items[index + 27].takeOrEmpty()

            val slot = WardrobeSlot(id, mutableListOf(slot1, slot2, slot3, slot4), locked)

            LoadoutStorage.updateEquipmentSlot(slot)
        }

        if (!foundCurrentSlot && isCurrentSlotInCurrentPage()) {
            LoadoutStorage.updateCurrentEquipmentSlot(null)
            if (previousSlot != null) clearPotentiallyDesyncedEquipment()
        }
    }

    fun isCurrentSlotInCurrentPage(): Boolean {
        val slot = currentSlot ?: return false
        val first = (currentPage - 1) * WARDROBE_SLOTS_PER_PAGE + 1
        val last = first + WARDROBE_SLOTS_PER_PAGE - 1
        return slot in first..last
    }

    fun clearPotentiallyDesyncedEquipment() {
        val storedEquipmentUUIDs =
            EquipmentSlot.entries.associateWith { equipmentSlot -> EquipmentAPI.islandEquipment[equipmentSlot]?.getData(DataTypes.UUID) }
        val previousEquipmentWardrobeSlotUUIDs =
            EquipmentSlot.entries.associateWith { equipmentSlot -> slots[previousSlot!!.minus(1)].slots[equipmentSlot.ordinal].getData(DataTypes.UUID) }

        previousEquipmentWardrobeSlotUUIDs.forEach { (equipmentSlot, previousUUID) ->
            if (previousUUID == storedEquipmentUUIDs[equipmentSlot]) {
                EquipmentStorage.setEquipment(equipmentSlot, ItemStack.EMPTY)
            }
        }

        previousSlot = null
    }

    @Subscription
    fun onInventoryUpdate(event: InventoryChangeEvent) {
        inWardrobe = inventoryNameRegex.matches(event.title)

        if (inWardrobe) processInventory(event.title, event.itemStacks)
    }

    @Subscription
    fun onInventoryOpen(event: ContainerInitializedEvent) {
        inWardrobe = inventoryNameRegex.matches(event.title)

        if (inWardrobe) processInventory(event.title, event.itemStacks)
    }

    @Subscription
    fun onInventoryClose(event: ContainerCloseEvent) {
        inWardrobe = false
        currentPage = 0
    }

    @Subscription
    fun onProfileSwitch(event: ProfileChangeEvent) {
        val slotCount = max(
            slots.size.roundToNextMultipleOf(WARDROBE_SLOTS_PER_PAGE),
            WARDROBE_SLOTS_PER_PAGE * 3,
        )
        repeat(slotCount) { index ->
            val incr = index + 1
            val foundSlot = slots.any { it.id == incr }
            if (!foundSlot) {
                val emptySlot = WardrobeSlot(incr, emptyEquipment, true)
                LoadoutStorage.updateEquipmentSlot(emptySlot)
            }
        }
    }

    @Subscription
    context(event: LoadoutChangeEvent)
    fun onLoadoutSwitch() {
        LoadoutStorage.equipment?.currentSlot = event.new?.equipment.value() ?: return
        if (currentSlot != -1) EquipmentStorage.setEquipment(LoadoutStorage.equipment?.slots[currentSlot!!])
        debugString(loadoutDebug) { "Setting equipment!" }
    }


    private fun ItemStack.takeOrEmpty() = takeIf { it !in ItemTag.GLASS_PANES } ?: ItemStack.EMPTY

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi wardrobe equipment") {
            then("copy") {
                callback {
                    val currentSlot = "Current Slot: $currentSlot"
                    val slots =
                        slots.map { "Id: ${it.id} - Equipment: ${it.slots.map { a -> a.hoverName.stripped }} - Locked: ${it.locked}" }

                    Text.of("[SkyBlockAPI] Copied Equipment Wardrobe Data to clipboard.") {
                        this.color = TextColor.YELLOW
                    }.send()

                    McClient.clipboard = "$currentSlot\n${slots.joinToString("\n")}"
                }
            }
            then("reset") {
                callback {
                    Text.of("[SkyBlockAPI] Reset Equipment Wardrobe Data.") {
                        this.color = TextColor.YELLOW
                    }.send()
                    LoadoutStorage.clearEquipment()
                }
            }
        }
    }
}
