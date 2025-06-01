package tech.thatgravyboat.skyblockapi.api.area.farming.garden

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

enum class Crop(val tool: Tool, icon: Item) {
    WHEAT(Tool.THEORETICAL_HOE_WHEAT, Items.WHEAT),
    CARROT(Tool.THEORETICAL_HOE_CARROT, Items.CARROT),
    POTATO(Tool.THEORETICAL_HOE_POTATO, Items.POTATO),
    PUMPKIN(Tool.PUMPKIN_DICER, Items.PUMPKIN),
    SUGAR_CANE(Tool.THEORETICAL_HOE_CANE, Items.SUGAR_CANE),
    MELON(Tool.MELON_DICER, Items.MELON_SLICE),
    CACTUS(Tool.CACTUS_KNIFE, Items.CACTUS),
    COCOA_BEANS(Tool.COCO_CHOPPER, Items.COCOA_BEANS),
    MUSHROOM(Tool.FUNGI_CUTTER, Items.RED_MUSHROOM),
    NETHER_WART(Tool.THEORETICAL_HOE_WARTS, Items.NETHER_WART),
    ;

    val icon: () -> ItemStack = { icon.defaultInstance }
}
