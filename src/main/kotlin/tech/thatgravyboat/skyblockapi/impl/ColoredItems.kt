package tech.thatgravyboat.skyblockapi.impl

//? < 26.4 {
/*import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

@Suppress("unused")
@Deprecated("With 26.1 dropped soon this will become useless, will be removed with 26.4")
public object ColoredItems {
    public val WHITE_WOOL: Item = Items.WOOL.white
    public val ORANGE_WOOL: Item = Items.WOOL.orange
    public val MAGENTA_WOOL: Item = Items.WOOL.magenta
    public val LIGHT_BLUE_WOOL: Item = Items.WOOL.lightBlue
    public val YELLOW_WOOL: Item = Items.WOOL.yellow
    public val LIME_WOOL: Item = Items.WOOL.lime
    public val PINK_WOOL: Item = Items.WOOL.pink
    public val GRAY_WOOL: Item = Items.WOOL.gray
    public val LIGHT_GRAY_WOOL: Item = Items.WOOL.lightGray
    public val CYAN_WOOL: Item = Items.WOOL.cyan
    public val PURPLE_WOOL: Item = Items.WOOL.purple
    public val BLUE_WOOL: Item = Items.WOOL.blue
    public val BROWN_WOOL: Item = Items.WOOL.brown
    public val GREEN_WOOL: Item = Items.WOOL.green
    public val RED_WOOL: Item = Items.WOOL.red
    public val BLACK_WOOL: Item = Items.WOOL.black

    public val WHITE_STAINED_GLASS: Item = Items.STAINED_GLASS.white
    public val ORANGE_STAINED_GLASS: Item = Items.STAINED_GLASS.orange
    public val MAGENTA_STAINED_GLASS: Item = Items.STAINED_GLASS.magenta
    public val LIGHT_BLUE_STAINED_GLASS: Item = Items.STAINED_GLASS.lightBlue
    public val YELLOW_STAINED_GLASS: Item = Items.STAINED_GLASS.yellow
    public val LIME_STAINED_GLASS: Item = Items.STAINED_GLASS.lime
    public val PINK_STAINED_GLASS: Item = Items.STAINED_GLASS.pink
    public val GRAY_STAINED_GLASS: Item = Items.STAINED_GLASS.gray
    public val LIGHT_GRAY_STAINED_GLASS: Item = Items.STAINED_GLASS.lightGray
    public val CYAN_STAINED_GLASS: Item = Items.STAINED_GLASS.cyan
    public val PURPLE_STAINED_GLASS: Item = Items.STAINED_GLASS.purple
    public val BLUE_STAINED_GLASS: Item = Items.STAINED_GLASS.blue
    public val BROWN_STAINED_GLASS: Item = Items.STAINED_GLASS.brown
    public val GREEN_STAINED_GLASS: Item = Items.STAINED_GLASS.green
    public val RED_STAINED_GLASS: Item = Items.STAINED_GLASS.red
    public val BLACK_STAINED_GLASS: Item = Items.STAINED_GLASS.black

    public val WHITE_TERRACOTTA: Item = Items.DYED_TERRACOTTA.white
    public val ORANGE_TERRACOTTA: Item = Items.DYED_TERRACOTTA.orange
    public val MAGENTA_TERRACOTTA: Item = Items.DYED_TERRACOTTA.magenta
    public val LIGHT_BLUE_TERRACOTTA: Item = Items.DYED_TERRACOTTA.lightBlue
    public val YELLOW_TERRACOTTA: Item = Items.DYED_TERRACOTTA.yellow
    public val LIME_TERRACOTTA: Item = Items.DYED_TERRACOTTA.lime
    public val PINK_TERRACOTTA: Item = Items.DYED_TERRACOTTA.pink
    public val GRAY_TERRACOTTA: Item = Items.DYED_TERRACOTTA.gray
    public val LIGHT_GRAY_TERRACOTTA: Item = Items.DYED_TERRACOTTA.lightGray
    public val CYAN_TERRACOTTA: Item = Items.DYED_TERRACOTTA.cyan
    public val PURPLE_TERRACOTTA: Item = Items.DYED_TERRACOTTA.purple
    public val BLUE_TERRACOTTA: Item = Items.DYED_TERRACOTTA.blue
    public val BROWN_TERRACOTTA: Item = Items.DYED_TERRACOTTA.brown
    public val GREEN_TERRACOTTA: Item = Items.DYED_TERRACOTTA.green
    public val RED_TERRACOTTA: Item = Items.DYED_TERRACOTTA.red
    public val BLACK_TERRACOTTA: Item = Items.DYED_TERRACOTTA.black

    public val WHITE_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.white
    public val ORANGE_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.orange
    public val MAGENTA_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.magenta
    public val LIGHT_BLUE_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.lightBlue
    public val YELLOW_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.yellow
    public val LIME_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.lime
    public val PINK_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.pink
    public val GRAY_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.gray
    public val LIGHT_GRAY_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.lightGray
    public val CYAN_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.cyan
    public val PURPLE_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.purple
    public val BLUE_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.blue
    public val BROWN_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.brown
    public val GREEN_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.green
    public val RED_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.red
    public val BLACK_STAINED_GLASS_PANE: Item = Items.STAINED_GLASS_PANE.black

    public val WHITE_CARPET: Item = Items.CARPET.white
    public val ORANGE_CARPET: Item = Items.CARPET.orange
    public val MAGENTA_CARPET: Item = Items.CARPET.magenta
    public val LIGHT_BLUE_CARPET: Item = Items.CARPET.lightBlue
    public val YELLOW_CARPET: Item = Items.CARPET.yellow
    public val LIME_CARPET: Item = Items.CARPET.lime
    public val PINK_CARPET: Item = Items.CARPET.pink
    public val GRAY_CARPET: Item = Items.CARPET.gray
    public val LIGHT_GRAY_CARPET: Item = Items.CARPET.lightGray
    public val CYAN_CARPET: Item = Items.CARPET.cyan
    public val PURPLE_CARPET: Item = Items.CARPET.purple
    public val BLUE_CARPET: Item = Items.CARPET.blue
    public val BROWN_CARPET: Item = Items.CARPET.brown
    public val GREEN_CARPET: Item = Items.CARPET.green
    public val RED_CARPET: Item = Items.CARPET.red
    public val BLACK_CARPET: Item = Items.CARPET.black

    public val WHITE_DYE: Item = Items.DYE.white
    public val ORANGE_DYE: Item = Items.DYE.orange
    public val MAGENTA_DYE: Item = Items.DYE.magenta
    public val LIGHT_BLUE_DYE: Item = Items.DYE.lightBlue
    public val YELLOW_DYE: Item = Items.DYE.yellow
    public val LIME_DYE: Item = Items.DYE.lime
    public val PINK_DYE: Item = Items.DYE.pink
    public val GRAY_DYE: Item = Items.DYE.gray
    public val LIGHT_GRAY_DYE: Item = Items.DYE.lightGray
    public val CYAN_DYE: Item = Items.DYE.cyan
    public val PURPLE_DYE: Item = Items.DYE.purple
    public val BLUE_DYE: Item = Items.DYE.blue
    public val BROWN_DYE: Item = Items.DYE.brown
    public val GREEN_DYE: Item = Items.DYE.green
    public val RED_DYE: Item = Items.DYE.red
    public val BLACK_DYE: Item = Items.DYE.black

    public val WHITE_BED: Item = Items.BED.white
    public val ORANGE_BED: Item = Items.BED.orange
    public val MAGENTA_BED: Item = Items.BED.magenta
    public val LIGHT_BLUE_BED: Item = Items.BED.lightBlue
    public val YELLOW_BED: Item = Items.BED.yellow
    public val LIME_BED: Item = Items.BED.lime
    public val PINK_BED: Item = Items.BED.pink
    public val GRAY_BED: Item = Items.BED.gray
    public val LIGHT_GRAY_BED: Item = Items.BED.lightGray
    public val CYAN_BED: Item = Items.BED.cyan
    public val PURPLE_BED: Item = Items.BED.purple
    public val BLUE_BED: Item = Items.BED.blue
    public val BROWN_BED: Item = Items.BED.brown
    public val GREEN_BED: Item = Items.BED.green
    public val RED_BED: Item = Items.BED.red
    public val BLACK_BED: Item = Items.BED.black

    public val WHITE_BANNER: Item = Items.BANNER.white
    public val ORANGE_BANNER: Item = Items.BANNER.orange
    public val MAGENTA_BANNER: Item = Items.BANNER.magenta
    public val LIGHT_BLUE_BANNER: Item = Items.BANNER.lightBlue
    public val YELLOW_BANNER: Item = Items.BANNER.yellow
    public val LIME_BANNER: Item = Items.BANNER.lime
    public val PINK_BANNER: Item = Items.BANNER.pink
    public val GRAY_BANNER: Item = Items.BANNER.gray
    public val LIGHT_GRAY_BANNER: Item = Items.BANNER.lightGray
    public val CYAN_BANNER: Item = Items.BANNER.cyan
    public val PURPLE_BANNER: Item = Items.BANNER.purple
    public val BLUE_BANNER: Item = Items.BANNER.blue
    public val BROWN_BANNER: Item = Items.BANNER.brown
    public val GREEN_BANNER: Item = Items.BANNER.green
    public val RED_BANNER: Item = Items.BANNER.red
    public val BLACK_BANNER: Item = Items.BANNER.black

    public val WHITE_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.white
    public val ORANGE_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.orange
    public val MAGENTA_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.magenta
    public val LIGHT_BLUE_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.lightBlue
    public val YELLOW_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.yellow
    public val LIME_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.lime
    public val PINK_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.pink
    public val GRAY_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.gray
    public val LIGHT_GRAY_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.lightGray
    public val CYAN_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.cyan
    public val PURPLE_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.purple
    public val BLUE_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.blue
    public val BROWN_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.brown
    public val GREEN_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.green
    public val RED_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.red
    public val BLACK_SHULKER_BOX: Item = Items.DYED_SHULKER_BOX.black
}
*///?}
