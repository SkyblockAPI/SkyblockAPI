package tech.thatgravyboat.skyblockapi.impl.tagkey

import net.fabricmc.fabric.api.tag.client.v1.ClientTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import tech.thatgravyboat.skyblockapi.utils.extentions.getItemModel

interface BaseTagKey<T> {
    val key: TagKey<T>

    operator fun contains(element: T): Boolean = ClientTags.isInWithLocalFallback(key, element)
}

interface BlockTagKey : BaseTagKey<Block> {
    override val key: TagKey<Block>

    override operator fun contains(element: Block): Boolean = ClientTags.isInWithLocalFallback(key, element)
}

interface ItemTagKey : BaseTagKey<Item> {
    override val key: TagKey<Item>

    operator fun contains(stack: ItemStack): Boolean = stack.item in this
    override operator fun contains(element: Item): Boolean = ClientTags.isInWithLocalFallback(key, element)
    operator fun contains(element: ItemLike): Boolean = ClientTags.isInWithLocalFallback(key, element.asItem())
}

interface ItemModelTagKey : ItemTagKey {
    override operator fun contains(stack: ItemStack): Boolean = stack.getItemModel() in this
}
