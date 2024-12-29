package tech.thatgravyboat.skyblockapi.api.profile.equipment

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.EquipmentStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.modules.Module
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

    val equipment get(): Map<EquipmentSlot, ItemStack> = EquipmentStorage.equipment

    fun getEquipment(slot: EquipmentSlot): ItemStack = equipment[slot] ?: ItemStack.EMPTY

    @Subscription
    fun onInventoryFullyLoad(event: ContainerInitializedEvent) {
        if (!inventoryNameRegex.matches(event.title)) return
        EquipmentSlot.entries.forEach {
            handleInventoryItem(it, event.itemStacks[it.slot])
        }
    }

    @Subscription
    fun onInventoryChange(event: ContainerChangeEvent) {
        if (!inventoryNameRegex.matches(event.title)) return
        val slot = EquipmentSlot.entries.find { it.slot == event.slot } ?: return
        handleInventoryItem(slot, event.item)
    }

    private fun handleInventoryItem(slot: EquipmentSlot, itemStack: ItemStack) {
        val item = if (itemStack.item == Items.LIGHT_GRAY_STAINED_GLASS_PANE) ItemStack.EMPTY
        else itemStack
        EquipmentStorage.setEquipment(slot, item)
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
            lastClickedEquipment = null
        }
    }

}
