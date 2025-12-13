package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.WardrobeStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
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
object WardrobeAPI {
    private val wardrobeGroup = RegexGroup.INVENTORY.group("wardrobe")

    private val inventoryNameRegex = wardrobeGroup.create(
        "title",
        "Wardrobe \\((?<currentPage>\\d+)/\\d+\\)",
    )

    private val equippedRegex = wardrobeGroup.create(
        "equip",
        "Slot \\d+: Equipped",
    )

    private val emptyArmor = mutableListOf(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY)

    var inWardrobe = false
        private set

    /** 0 if not in wardrobe */
    var currentPage = 0
        private set

    val slots get() = WardrobeStorage.slots.toList()
    val currentSlot: Int? get() = WardrobeStorage.currentSlot


    private fun processInventory(title: String, items: List<ItemStack>) {
        inventoryNameRegex.match(title, "currentPage") { (cp) ->
            cp.toIntOrNull()?.let { currentPage = it }
        }

        var foundCurrentSlot = false

        for (index in 0..<WARDROBE_SLOTS_PER_PAGE) {
            val selectStack = items[index + SELECT_START_INDEX]
            val id = WARDROBE_SLOTS_PER_PAGE * (currentPage - 1) + index + 1
            var locked = false

            if (selectStack.item == Items.RED_DYE) {
                locked = true
            } else if (equippedRegex.match(selectStack.hoverName.stripped)) {
                WardrobeStorage.updateCurrentSlot(id)
                foundCurrentSlot = true
            }

            val helmetStack = items[index].takeOrEmpty()
            val chestplateStack = items[index + 9].takeOrEmpty()
            val leggingsStack = items[index + 18].takeOrEmpty()
            val bootsStack = items[index + 27].takeOrEmpty()

            val slot = WardrobeSlot(id, mutableListOf(helmetStack, chestplateStack, leggingsStack, bootsStack), locked)

            WardrobeStorage.updateSlot(slot)
        }

        if (!foundCurrentSlot && isCurrentSlotInCurrentPage()) {
            WardrobeStorage.updateCurrentSlot(null)
        }
    }

    fun isCurrentSlotInCurrentPage(): Boolean {
        val slot = WardrobeStorage.currentSlot ?: return false
        val first = (currentPage - 1) * WARDROBE_SLOTS_PER_PAGE + 1
        val last = first + WARDROBE_SLOTS_PER_PAGE - 1
        return slot in first..last
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
        	WardrobeStorage.slots.size.roundToNextMultipleOf(WARDROBE_SLOTS_PER_PAGE),
        	WARDROBE_SLOTS_PER_PAGE * 3
        )
        repeat(slotCount) { index ->
            val incr = index + 1
            val foundSlot = slots.any { it.id == incr }
            if (!foundSlot) {
                val emptySlot = WardrobeSlot(incr, emptyArmor, true)
                WardrobeStorage.updateSlot(emptySlot)
            }
        }
    }

    private fun ItemStack.takeOrEmpty() = takeIf { it !in ItemTag.GLASS_PANES } ?: ItemStack.EMPTY

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi wardrobe") {
            then("copy") {
                callback {
                    val currentSlot = "Current Slot: $currentSlot"
                    val slots =
                        slots.map { "Id: ${it.id} - Armor: ${it.armor.map { a -> a.hoverName.stripped }} - Locked: ${it.locked}" }

                    Text.of("[SkyBlockAPI] Copied Wardrobe Data to clipboard.") {
                        this.color = TextColor.YELLOW
                    }.send()

                    McClient.clipboard = "$currentSlot\n${slots.joinToString("\n")}"
                }
            }
            then("reset") {
                callback {
                    Text.of("[SkyBlockAPI] Reset Wardrobe Data.") {
                        this.color = TextColor.YELLOW
                    }.send()
                    WardrobeStorage.clear()
                }
            }
        }
    }
}
