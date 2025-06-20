package tech.thatgravyboat.skyblockapi.api.area.farming.garden

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

enum class Crop(val tool: FarmingTool, icon: Item) {
    WHEAT(FarmingTool.THEORETICAL_HOE_WHEAT, Items.WHEAT),
    CARROT(FarmingTool.THEORETICAL_HOE_CARROT, Items.CARROT),
    POTATO(FarmingTool.THEORETICAL_HOE_POTATO, Items.POTATO),
    PUMPKIN(FarmingTool.PUMPKIN_DICER, Items.PUMPKIN),
    SUGAR_CANE(FarmingTool.THEORETICAL_HOE_CANE, Items.SUGAR_CANE),
    MELON(FarmingTool.MELON_DICER, Items.MELON_SLICE),
    CACTUS(FarmingTool.CACTUS_KNIFE, Items.CACTUS),
    COCOA_BEANS(FarmingTool.COCO_CHOPPER, Items.COCOA_BEANS),
    MUSHROOM(FarmingTool.FUNGI_CUTTER, Items.RED_MUSHROOM),
    NETHER_WART(FarmingTool.THEORETICAL_HOE_WARTS, Items.NETHER_WART),
    ;

    val icon: () -> ItemStack = { icon.defaultInstance }
}
