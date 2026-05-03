package tech.thatgravyboat.skyblockapi.impl.tagkey

import net.fabricmc.fabric.api.tag.client.v1.ClientTags
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import tech.thatgravyboat.skyblockapi.platform.identifier
import tech.thatgravyboat.skyblockapi.utils.extentions.getItemModel
import kotlin.jvm.optionals.getOrNull

interface BaseTagKey<T : Any> {
    val key: TagKey<T>

    @Suppress("UNCHECKED_CAST")
    private val registry: Registry<T>? get() = BuiltInRegistries.REGISTRY
        .getOptional(key.registry().identifier)
        .getOrNull() as Registry<T>?

    operator fun contains(element: T): Boolean = element
        ?.let { registry?.getResourceKey(it) }
        ?.map { ClientTags.isInLocal(key, it) }
        ?.getOrNull()
        ?: false
}

interface BlockTagKey : BaseTagKey<Block> {
    override val key: TagKey<Block>

    override operator fun contains(element: Block): Boolean = super.contains(element)
}

interface ItemTagKey : BaseTagKey<Item> {
    override val key: TagKey<Item>

    operator fun contains(stack: ItemStack): Boolean = stack.item in this
    operator fun contains(element: ItemLike): Boolean = element.asItem() in this
    override operator fun contains(element: Item): Boolean = super.contains(element)
}

interface ItemModelTagKey : ItemTagKey {
    override operator fun contains(stack: ItemStack): Boolean = stack.getItemModel() in this
}
