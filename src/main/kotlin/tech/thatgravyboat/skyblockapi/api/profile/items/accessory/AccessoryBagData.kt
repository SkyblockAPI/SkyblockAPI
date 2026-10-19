package tech.thatgravyboat.skyblockapi.api.profile.items.accessory

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack

// TODO: move somewhere else once its internal in all versions
@GenerateCodec
//~ if >= 26.4 'public' -> 'internal'
public data class AccessoryBagItem(
    val item: ItemStack,
    val page: Int,
    val slot: Int,
)
