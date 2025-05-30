package tech.thatgravyboat.skyblockapi.api.profile.items.vault

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.VaultStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent

@Module
object VaultAPI {

    @Subscription
    @MustBeContainer
    @InventoryTitle("Personal Vault")
    fun InventoryChangeEvent.onInventory() {
        if (this.slot.index == 0) VaultStorage.invalidate()
        VaultStorage.addItem(this.item)
    }

    fun getItems(): List<ItemStack> = VaultStorage.getItems().toList()
}
