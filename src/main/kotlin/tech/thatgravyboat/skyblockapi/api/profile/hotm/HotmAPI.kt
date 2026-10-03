package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.HotmStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.StringMatch
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeAPI
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeType
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.extentions.getItemModel

@Module
object HotmAPI : SkillTreeAPI<HotmPerk, HotmAPI>(
    name = "hotm",
    perkItems = ItemTag.HOTM_PERK_ITEMS,
    storage = HotmStorage,
    identifier = "Mountain",
    type = SkillTreeType.Hotm
) {
    private var holdingBlueOmelette = false

    @Subscription(ContainerInitializedEvent::class)
    fun onInventoryOpen() {
        holdingBlueOmelette = McPlayer.heldItem.getData(DataTypes.UPGRADE_MODULE).equals("GOBLIN_OMELETTE_BLUE_CHEESE", true)
    }

    override fun adjustLevel(level: Int): Int = if (holdingBlueOmelette) (level - 1).coerceAtLeast(1) else level

    override fun LoadoutSlot.getLoadoutMatch(): StringMatch? = hotm
    override fun isUnlocked(item: ItemStack): Boolean {
        return item.getItemModel().let { it != Items.COAL && it != Items.COAL_BLOCK }
    }
    override fun createPerk(level: Int, unlocked: Boolean, disabled: Boolean) = HotmPerk(level, unlocked, disabled)
}
