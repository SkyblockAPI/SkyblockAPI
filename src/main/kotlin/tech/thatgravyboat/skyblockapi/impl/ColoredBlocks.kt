package tech.thatgravyboat.skyblockapi.impl

//? < 26.4 {
/*import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

@Suppress("unused")
@Deprecated("With 26.1 dropped soon this will become useless, will be removed with 26.4")
object ColoredBlocks {
    val WHITE_WOOL: Block = Blocks.WOOL.white
    val ORANGE_WOOL: Block = Blocks.WOOL.orange
    val MAGENTA_WOOL: Block = Blocks.WOOL.magenta
    val LIGHT_BLUE_WOOL: Block = Blocks.WOOL.lightBlue
    val YELLOW_WOOL: Block = Blocks.WOOL.yellow
    val LIME_WOOL: Block = Blocks.WOOL.lime
    val PINK_WOOL: Block = Blocks.WOOL.pink
    val GRAY_WOOL: Block = Blocks.WOOL.gray
    val LIGHT_GRAY_WOOL: Block = Blocks.WOOL.lightGray
    val CYAN_WOOL: Block = Blocks.WOOL.cyan
    val PURPLE_WOOL: Block = Blocks.WOOL.purple
    val BLUE_WOOL: Block = Blocks.WOOL.blue
    val BROWN_WOOL: Block = Blocks.WOOL.brown
    val GREEN_WOOL: Block = Blocks.WOOL.green
    val RED_WOOL: Block = Blocks.WOOL.red
    val BLACK_WOOL: Block = Blocks.WOOL.black

    val WHITE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.white
    val ORANGE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.orange
    val MAGENTA_STAINED_GLASS: Block = Blocks.STAINED_GLASS.magenta
    val LIGHT_BLUE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.lightBlue
    val YELLOW_STAINED_GLASS: Block = Blocks.STAINED_GLASS.yellow
    val LIME_STAINED_GLASS: Block = Blocks.STAINED_GLASS.lime
    val PINK_STAINED_GLASS: Block = Blocks.STAINED_GLASS.pink
    val GRAY_STAINED_GLASS: Block = Blocks.STAINED_GLASS.gray
    val LIGHT_GRAY_STAINED_GLASS: Block = Blocks.STAINED_GLASS.lightGray
    val CYAN_STAINED_GLASS: Block = Blocks.STAINED_GLASS.cyan
    val PURPLE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.purple
    val BLUE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.blue
    val BROWN_STAINED_GLASS: Block = Blocks.STAINED_GLASS.brown
    val GREEN_STAINED_GLASS: Block = Blocks.STAINED_GLASS.green
    val RED_STAINED_GLASS: Block = Blocks.STAINED_GLASS.red
    val BLACK_STAINED_GLASS: Block = Blocks.STAINED_GLASS.black

    val WHITE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.white
    val ORANGE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.orange
    val MAGENTA_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.magenta
    val LIGHT_BLUE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.lightBlue
    val YELLOW_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.yellow
    val LIME_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.lime
    val PINK_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.pink
    val GRAY_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.gray
    val LIGHT_GRAY_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.lightGray
    val CYAN_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.cyan
    val PURPLE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.purple
    val BLUE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.blue
    val BROWN_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.brown
    val GREEN_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.green
    val RED_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.red
    val BLACK_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.black

    val WHITE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.white
    val ORANGE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.orange
    val MAGENTA_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.magenta
    val LIGHT_BLUE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.lightBlue
    val YELLOW_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.yellow
    val LIME_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.lime
    val PINK_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.pink
    val GRAY_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.gray
    val LIGHT_GRAY_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.lightGray
    val CYAN_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.cyan
    val PURPLE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.purple
    val BLUE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.blue
    val BROWN_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.brown
    val GREEN_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.green
    val RED_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.red
    val BLACK_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.black

    val WHITE_CARPET: Block = Blocks.CARPET.white
    val ORANGE_CARPET: Block = Blocks.CARPET.orange
    val MAGENTA_CARPET: Block = Blocks.CARPET.magenta
    val LIGHT_BLUE_CARPET: Block = Blocks.CARPET.lightBlue
    val YELLOW_CARPET: Block = Blocks.CARPET.yellow
    val LIME_CARPET: Block = Blocks.CARPET.lime
    val PINK_CARPET: Block = Blocks.CARPET.pink
    val GRAY_CARPET: Block = Blocks.CARPET.gray
    val LIGHT_GRAY_CARPET: Block = Blocks.CARPET.lightGray
    val CYAN_CARPET: Block = Blocks.CARPET.cyan
    val PURPLE_CARPET: Block = Blocks.CARPET.purple
    val BLUE_CARPET: Block = Blocks.CARPET.blue
    val BROWN_CARPET: Block = Blocks.CARPET.brown
    val GREEN_CARPET: Block = Blocks.CARPET.green
    val RED_CARPET: Block = Blocks.CARPET.red
    val BLACK_CARPET: Block = Blocks.CARPET.black

    val WHITE_BED: Block = Blocks.BED.white
    val ORANGE_BED: Block = Blocks.BED.orange
    val MAGENTA_BED: Block = Blocks.BED.magenta
    val LIGHT_BLUE_BED: Block = Blocks.BED.lightBlue
    val YELLOW_BED: Block = Blocks.BED.yellow
    val LIME_BED: Block = Blocks.BED.lime
    val PINK_BED: Block = Blocks.BED.pink
    val GRAY_BED: Block = Blocks.BED.gray
    val LIGHT_GRAY_BED: Block = Blocks.BED.lightGray
    val CYAN_BED: Block = Blocks.BED.cyan
    val PURPLE_BED: Block = Blocks.BED.purple
    val BLUE_BED: Block = Blocks.BED.blue
    val BROWN_BED: Block = Blocks.BED.brown
    val GREEN_BED: Block = Blocks.BED.green
    val RED_BED: Block = Blocks.BED.red
    val BLACK_BED: Block = Blocks.BED.black

    val WHITE_BANNER: Block = Blocks.BANNER.white
    val ORANGE_BANNER: Block = Blocks.BANNER.orange
    val MAGENTA_BANNER: Block = Blocks.BANNER.magenta
    val LIGHT_BLUE_BANNER: Block = Blocks.BANNER.lightBlue
    val YELLOW_BANNER: Block = Blocks.BANNER.yellow
    val LIME_BANNER: Block = Blocks.BANNER.lime
    val PINK_BANNER: Block = Blocks.BANNER.pink
    val GRAY_BANNER: Block = Blocks.BANNER.gray
    val LIGHT_GRAY_BANNER: Block = Blocks.BANNER.lightGray
    val CYAN_BANNER: Block = Blocks.BANNER.cyan
    val PURPLE_BANNER: Block = Blocks.BANNER.purple
    val BLUE_BANNER: Block = Blocks.BANNER.blue
    val BROWN_BANNER: Block = Blocks.BANNER.brown
    val GREEN_BANNER: Block = Blocks.BANNER.green
    val RED_BANNER: Block = Blocks.BANNER.red
    val BLACK_BANNER: Block = Blocks.BANNER.black

    val WHITE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.white
    val ORANGE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.orange
    val MAGENTA_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.magenta
    val LIGHT_BLUE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.lightBlue
    val YELLOW_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.yellow
    val LIME_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.lime
    val PINK_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.pink
    val GRAY_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.gray
    val LIGHT_GRAY_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.lightGray
    val CYAN_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.cyan
    val PURPLE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.purple
    val BLUE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.blue
    val BROWN_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.brown
    val GREEN_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.green
    val RED_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.red
    val BLACK_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.black
}
*///?}
