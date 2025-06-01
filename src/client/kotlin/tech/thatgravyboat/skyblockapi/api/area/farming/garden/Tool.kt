package tech.thatgravyboat.skyblockapi.api.area.farming.garden

enum class Tool(val multipleTiers: Boolean = true) {
    THEORETICAL_HOE_WHEAT,
    THEORETICAL_HOE_CARROT,
    THEORETICAL_HOE_POTATO,
    PUMPKIN_DICER,
    THEORETICAL_HOE_CANE,
    MELON_DICER,
    CACTUS_KNIFE(false),
    COCO_CHOPPER(false),
    FUNGI_CUTTER(false),
    THEORETICAL_HOE_WARTS(false)
    ;
}
