package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

//? < 26.3 {
/*import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeSlot as NewWardrobeSlot

@Deprecated("Replace with WardrobeAPI", ReplaceWith("tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeSlot"))
data class WardrobeSlot(
    val id: Int,
    val armor: MutableList<ItemStack>,
    val locked: Boolean,
)

internal fun NewWardrobeSlot.into() = WardrobeSlot(id, slots, locked)
*///?}
