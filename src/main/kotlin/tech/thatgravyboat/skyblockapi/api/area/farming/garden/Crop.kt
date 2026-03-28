package tech.thatgravyboat.skyblockapi.api.area.farming.garden

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.StemBlock
import net.minecraft.world.level.block.state.BlockState
//? < 26.1
//import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

enum class Crop(val tool: FarmingTool, vararg block: Block, skyBlockId: String? = null) {
    WHEAT(FarmingTool.THEORETICAL_HOE_WHEAT, Blocks.WHEAT),
    CARROT(FarmingTool.THEORETICAL_HOE_CARROT, Blocks.CARROTS),
    POTATO(FarmingTool.THEORETICAL_HOE_POTATO, Blocks.POTATOES),
    PUMPKIN(FarmingTool.PUMPKIN_DICER, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN),
    SUGAR_CANE(FarmingTool.THEORETICAL_HOE_CANE, Blocks.SUGAR_CANE),
    MELON(FarmingTool.MELON_DICER, Blocks.MELON),
    CACTUS(FarmingTool.CACTUS_KNIFE, Blocks.CACTUS),
    COCOA_BEANS(FarmingTool.COCO_CHOPPER, Blocks.COCOA, skyBlockId = "ink_sack:3"),
    MUSHROOM(FarmingTool.FUNGI_CUTTER, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, skyBlockId = "red_mushroom_block"),
    NETHER_WART(FarmingTool.THEORETICAL_HOE_WARTS, Blocks.NETHER_WART, skyBlockId = "nether_stalk"),
    SUNFLOWER(FarmingTool.THEORETICAL_HOE_SUNFLOWER, Blocks.SUNFLOWER, skyBlockId = "double_plant") {
        override fun isCrop(state: BlockState): Boolean = isSunflowerOrMoonFlower(state)
    },
    MOONFLOWER(FarmingTool.THEORETICAL_HOE_SUNFLOWER, Blocks.SUNFLOWER) {
        override fun isCrop(state: BlockState): Boolean = isSunflowerOrMoonFlower(state)
    },
    WILD_ROSE(FarmingTool.THEORETICAL_HOE_WILD_ROSE, Blocks.ROSE_BUSH) {
        override fun isCrop(state: BlockState): Boolean {
            return when (state.block) {
                Blocks.ROSE_BUSH, Blocks.POPPY -> true
                Blocks.MELON_STEM -> isStemOfAge(state, 3)
                else -> false
            }
        }
    },
    ;

    val blocks: Set<Block> = block.toSet()
    val skyBlockId: SkyBlockId = SkyBlockId.item(skyBlockId ?: name)

    open fun isCrop(state: BlockState): Boolean = state.block in blocks

    //? < 26.1
    //@RemoveNextVersion(ReplaceWith("item")) val icon: () -> ItemStack = { this.skyBlockId.toItem() }
    val item: ItemStack get() = this.skyBlockId.toItem()

    companion object {
        private fun isStemOfAge(state: BlockState, vararg ages: Int): Boolean = state.getValue(StemBlock.AGE) in ages
        private fun isSunflowerOrMoonFlower(state: BlockState): Boolean {
            return when (state.block) {
                Blocks.SUNFLOWER -> true
                Blocks.MELON_STEM -> isStemOfAge(state, 2, 5)
                else -> false
            }
        }
    }
}
