package tech.thatgravyboat.skyblockapi.api.profile.items.equipment

import me.owdding.ktmodules.Module
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.EquipmentStorage
import tech.thatgravyboat.skyblockapi.api.data.stored.LoadoutStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.DebugBuilder
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.EquipmentWardrobeAPI
import tech.thatgravyboat.skyblockapi.api.profile.quiver.QuiverAPI.arrows
import tech.thatgravyboat.skyblockapi.api.profile.quiver.QuiverAPI.currentAmount
import tech.thatgravyboat.skyblockapi.api.profile.quiver.QuiverAPI.currentArrow
import tech.thatgravyboat.skyblockapi.impl.ColoredItems
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find

@Module
object EquipmentAPI {

    private val inventoryNameRegex = RegexGroup.INVENTORY.group("equipment").create(
        "title",
        "Your Equipment and Stats",
    )

    private val chatEquipRegex = RegexGroup.CHAT.group("equipment").create(
        "equip",
        "You equipped a (?<item>.+)!",
    )

    private var lastClickedEquipment: Pair<ItemStack, EquipmentSlot>? = null

    val normalEquipment: MutableMap<EquipmentSlot, ItemStack> get() = EquipmentWardrobeAPI.currentSet.toMutableMap()
    fun getNormalEquipment(slot: EquipmentSlot): ItemStack = normalEquipment[slot] ?: ItemStack.EMPTY

    val riftEquipment: MutableMap<EquipmentSlot, ItemStack> get() = EquipmentStorage.riftEquipment
    fun getRiftEquipment(slot: EquipmentSlot): ItemStack = riftEquipment[slot] ?: ItemStack.EMPTY

    val islandEquipment: Map<EquipmentSlot, ItemStack> get() = if (SkyBlockIsland.THE_RIFT.inIsland()) riftEquipment else normalEquipment
    fun getIslandEquipment(slot: EquipmentSlot): ItemStack = islandEquipment[slot] ?: ItemStack.EMPTY

    @Subscription
    fun onInventoryFullyLoad(event: ContainerInitializedEvent) {
        if (!inventoryNameRegex.matches(event.title)) return
        EquipmentSlot.entries.forEach {
            handleInventoryItem(it, event.containerItems[it.slot])
        }
    }

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (!inventoryNameRegex.matches(event.title)) return
        if (event.isInPlayerInventory) return
        val slot = EquipmentSlot.entries.find { it.slot == event.slot.index } ?: return
        handleInventoryItem(slot, event.item)
    }

    private fun handleInventoryItem(slot: EquipmentSlot, itemStack: ItemStack) {
        val item = if (itemStack.item == ColoredItems.LIGHT_GRAY_STAINED_GLASS_PANE) ItemStack.EMPTY
        else {
            val category = itemStack.getData(DataTypes.CATEGORY) ?: return
            if (category !in slot.categories) return
            itemStack
        }

        EquipmentStorage.setEquipment(slot, item)
        syncWardrobe(slot, item)
    }

    @Subscription
    fun onRightClick(event: RightClickEvent) {
        val category = event.stack.getData(DataTypes.CATEGORY) ?: return
        val slot = EquipmentSlot.entries.find { category in it.categories } ?: return
        lastClickedEquipment = event.stack to slot
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        val (item, slot) = lastClickedEquipment ?: return
        chatEquipRegex.find(event.text, "item") { (itemName) ->
            if (item.cleanName != itemName) return@find

            EquipmentStorage.setEquipment(slot, item)
            syncWardrobe(slot, item)

            lastClickedEquipment = null
        }
    }

    private fun syncWardrobe(slot: EquipmentSlot, item: ItemStack) {
        if (SkyBlockIsland.THE_RIFT.inIsland()) return

        val currentSlotId = EquipmentWardrobeAPI.currentSlot ?: return
        val wardrobeSlot = EquipmentWardrobeAPI.slots.find { it.id == currentSlotId } ?: return

        val itemIndex = when (slot) {
            EquipmentSlot.NECKLACE -> 0
            EquipmentSlot.CLOAK -> 1
            EquipmentSlot.BELT -> 2
            EquipmentSlot.GLOVES -> 3
        }

        val newItems = wardrobeSlot.slots.toMutableList()
        newItems[itemIndex] = item

        val updatedSlot = wardrobeSlot.copy(slots = newItems)

        LoadoutStorage.updateEquipmentSlot(updatedSlot)
    }

    @ApiDebug("Equipment")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        fields(::normalEquipment, ::riftEquipment)
    }


}
