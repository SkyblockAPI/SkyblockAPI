package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.HotfStorage
import tech.thatgravyboat.skyblockapi.api.profile.hotx.HotxAPI
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemModelTag
import tech.thatgravyboat.skyblockapi.utils.extentions.getItemModel

@Module
object HotfAPI : HotxAPI<HotfData, HotfPerk>(
    regexGroup = "hotf",
    perkItems = ItemModelTag.HOTF_PERK_ITEMS,
    storage = HotfStorage,
    identifier = "Forest",
) {
    override fun isUnlocked(item: ItemStack): Boolean {
        return item.getItemModel().let { it != Items.MANGROVE_ROOTS && it != Items.PALE_OAK_SAPLING && it != Items.PALE_OAK_BUTTON }
    }

    override fun createPerk(level: Int, unlocked: Boolean, disabled: Boolean) = HotfPerk(level, unlocked, disabled)
}
