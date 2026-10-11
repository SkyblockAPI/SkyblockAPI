package tech.thatgravyboat.skyblockapi.impl

//? < 26.4 {
/*import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

@Suppress("unused")
@Deprecated("With 26.1 dropped soon this will become useless, will be removed with 26.4")
public object ColoredBlocks {
    public val WHITE_WOOL: Block = Blocks.WOOL.white
    public val ORANGE_WOOL: Block = Blocks.WOOL.orange
    public val MAGENTA_WOOL: Block = Blocks.WOOL.magenta
    public val LIGHT_BLUE_WOOL: Block = Blocks.WOOL.lightBlue
    public val YELLOW_WOOL: Block = Blocks.WOOL.yellow
    public val LIME_WOOL: Block = Blocks.WOOL.lime
    public val PINK_WOOL: Block = Blocks.WOOL.pink
    public val GRAY_WOOL: Block = Blocks.WOOL.gray
    public val LIGHT_GRAY_WOOL: Block = Blocks.WOOL.lightGray
    public val CYAN_WOOL: Block = Blocks.WOOL.cyan
    public val PURPLE_WOOL: Block = Blocks.WOOL.purple
    public val BLUE_WOOL: Block = Blocks.WOOL.blue
    public val BROWN_WOOL: Block = Blocks.WOOL.brown
    public val GREEN_WOOL: Block = Blocks.WOOL.green
    public val RED_WOOL: Block = Blocks.WOOL.red
    public val BLACK_WOOL: Block = Blocks.WOOL.black

    public val WHITE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.white
    public val ORANGE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.orange
    public val MAGENTA_STAINED_GLASS: Block = Blocks.STAINED_GLASS.magenta
    public val LIGHT_BLUE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.lightBlue
    public val YELLOW_STAINED_GLASS: Block = Blocks.STAINED_GLASS.yellow
    public val LIME_STAINED_GLASS: Block = Blocks.STAINED_GLASS.lime
    public val PINK_STAINED_GLASS: Block = Blocks.STAINED_GLASS.pink
    public val GRAY_STAINED_GLASS: Block = Blocks.STAINED_GLASS.gray
    public val LIGHT_GRAY_STAINED_GLASS: Block = Blocks.STAINED_GLASS.lightGray
    public val CYAN_STAINED_GLASS: Block = Blocks.STAINED_GLASS.cyan
    public val PURPLE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.purple
    public val BLUE_STAINED_GLASS: Block = Blocks.STAINED_GLASS.blue
    public val BROWN_STAINED_GLASS: Block = Blocks.STAINED_GLASS.brown
    public val GREEN_STAINED_GLASS: Block = Blocks.STAINED_GLASS.green
    public val RED_STAINED_GLASS: Block = Blocks.STAINED_GLASS.red
    public val BLACK_STAINED_GLASS: Block = Blocks.STAINED_GLASS.black

    public val WHITE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.white
    public val ORANGE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.orange
    public val MAGENTA_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.magenta
    public val LIGHT_BLUE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.lightBlue
    public val YELLOW_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.yellow
    public val LIME_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.lime
    public val PINK_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.pink
    public val GRAY_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.gray
    public val LIGHT_GRAY_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.lightGray
    public val CYAN_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.cyan
    public val PURPLE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.purple
    public val BLUE_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.blue
    public val BROWN_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.brown
    public val GREEN_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.green
    public val RED_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.red
    public val BLACK_TERRACOTTA: Block = Blocks.DYED_TERRACOTTA.black

    public val WHITE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.white
    public val ORANGE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.orange
    public val MAGENTA_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.magenta
    public val LIGHT_BLUE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.lightBlue
    public val YELLOW_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.yellow
    public val LIME_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.lime
    public val PINK_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.pink
    public val GRAY_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.gray
    public val LIGHT_GRAY_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.lightGray
    public val CYAN_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.cyan
    public val PURPLE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.purple
    public val BLUE_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.blue
    public val BROWN_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.brown
    public val GREEN_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.green
    public val RED_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.red
    public val BLACK_STAINED_GLASS_PANE: Block = Blocks.STAINED_GLASS_PANE.black

    public val WHITE_CARPET: Block = Blocks.CARPET.white
    public val ORANGE_CARPET: Block = Blocks.CARPET.orange
    public val MAGENTA_CARPET: Block = Blocks.CARPET.magenta
    public val LIGHT_BLUE_CARPET: Block = Blocks.CARPET.lightBlue
    public val YELLOW_CARPET: Block = Blocks.CARPET.yellow
    public val LIME_CARPET: Block = Blocks.CARPET.lime
    public val PINK_CARPET: Block = Blocks.CARPET.pink
    public val GRAY_CARPET: Block = Blocks.CARPET.gray
    public val LIGHT_GRAY_CARPET: Block = Blocks.CARPET.lightGray
    public val CYAN_CARPET: Block = Blocks.CARPET.cyan
    public val PURPLE_CARPET: Block = Blocks.CARPET.purple
    public val BLUE_CARPET: Block = Blocks.CARPET.blue
    public val BROWN_CARPET: Block = Blocks.CARPET.brown
    public val GREEN_CARPET: Block = Blocks.CARPET.green
    public val RED_CARPET: Block = Blocks.CARPET.red
    public val BLACK_CARPET: Block = Blocks.CARPET.black

    public val WHITE_BED: Block = Blocks.BED.white
    public val ORANGE_BED: Block = Blocks.BED.orange
    public val MAGENTA_BED: Block = Blocks.BED.magenta
    public val LIGHT_BLUE_BED: Block = Blocks.BED.lightBlue
    public val YELLOW_BED: Block = Blocks.BED.yellow
    public val LIME_BED: Block = Blocks.BED.lime
    public val PINK_BED: Block = Blocks.BED.pink
    public val GRAY_BED: Block = Blocks.BED.gray
    public val LIGHT_GRAY_BED: Block = Blocks.BED.lightGray
    public val CYAN_BED: Block = Blocks.BED.cyan
    public val PURPLE_BED: Block = Blocks.BED.purple
    public val BLUE_BED: Block = Blocks.BED.blue
    public val BROWN_BED: Block = Blocks.BED.brown
    public val GREEN_BED: Block = Blocks.BED.green
    public val RED_BED: Block = Blocks.BED.red
    public val BLACK_BED: Block = Blocks.BED.black

    public val WHITE_BANNER: Block = Blocks.BANNER.white
    public val ORANGE_BANNER: Block = Blocks.BANNER.orange
    public val MAGENTA_BANNER: Block = Blocks.BANNER.magenta
    public val LIGHT_BLUE_BANNER: Block = Blocks.BANNER.lightBlue
    public val YELLOW_BANNER: Block = Blocks.BANNER.yellow
    public val LIME_BANNER: Block = Blocks.BANNER.lime
    public val PINK_BANNER: Block = Blocks.BANNER.pink
    public val GRAY_BANNER: Block = Blocks.BANNER.gray
    public val LIGHT_GRAY_BANNER: Block = Blocks.BANNER.lightGray
    public val CYAN_BANNER: Block = Blocks.BANNER.cyan
    public val PURPLE_BANNER: Block = Blocks.BANNER.purple
    public val BLUE_BANNER: Block = Blocks.BANNER.blue
    public val BROWN_BANNER: Block = Blocks.BANNER.brown
    public val GREEN_BANNER: Block = Blocks.BANNER.green
    public val RED_BANNER: Block = Blocks.BANNER.red
    public val BLACK_BANNER: Block = Blocks.BANNER.black

    public val WHITE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.white
    public val ORANGE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.orange
    public val MAGENTA_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.magenta
    public val LIGHT_BLUE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.lightBlue
    public val YELLOW_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.yellow
    public val LIME_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.lime
    public val PINK_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.pink
    public val GRAY_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.gray
    public val LIGHT_GRAY_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.lightGray
    public val CYAN_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.cyan
    public val PURPLE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.purple
    public val BLUE_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.blue
    public val BROWN_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.brown
    public val GREEN_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.green
    public val RED_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.red
    public val BLACK_SHULKER_BOX: Block = Blocks.DYED_SHULKER_BOX.black
}
*///?}
