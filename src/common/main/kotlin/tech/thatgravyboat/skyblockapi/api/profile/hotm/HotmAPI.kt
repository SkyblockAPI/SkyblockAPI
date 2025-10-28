package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.HotmStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.profile.hotx.SkillTreeAPI
import tech.thatgravyboat.skyblockapi.api.profile.hotx.SkillTreeType
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag

@Module
object HotmAPI : SkillTreeAPI<HotmData, HotmPerk, HotmAPI>(
    name = "hotm",
    perkItems = ItemTag.HOTM_PERK_ITEMS,
    storage = HotmStorage,
    identifier = "Mountain",
    type = SkillTreeType.Hotm
) {
    private var holdingBlueOmelette = false

    @Subscription
    fun onInventoryOpen(event: ContainerInitializedEvent) {
        holdingBlueOmelette = McPlayer.self?.mainHandItem?.getData(DataTypes.UPGRADE_MODULE).equals("GOBLIN_OMELETTE_BLUE_CHEESE", true)
    }

    override fun adjustLevel(level: Int): Int = if (holdingBlueOmelette) (level - 1).coerceAtLeast(1) else level

    override fun isUnlocked(item: ItemStack): Boolean = !item.`is`(Items.COAL) && !item.`is`(Items.COAL_BLOCK)

    override fun createPerk(level: Int, unlocked: Boolean, disabled: Boolean) = HotmPerk(level, unlocked, disabled)
}
