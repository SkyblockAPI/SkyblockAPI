package tech.thatgravyboat.skyblockapi.utils.extentions

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.mixins.accessors.CustomDataAccessor
import tech.thatgravyboat.skyblockapi.platform.GameProfile
import tech.thatgravyboat.skyblockapi.platform.properties
import tech.thatgravyboat.skyblockapi.platform.toResolvableProfile
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Suppress("DEPRECATION")
public val ItemStack.tag: CompoundTag? get() = this[DataComponents.CUSTOM_DATA]?.copyTag()
public val ItemStack.unsafeTag: CompoundTag? get() = (this[DataComponents.CUSTOM_DATA] as? CustomDataAccessor)?.`skyblockapi$getTag`()
public fun ItemStack.getTag(key: String): Tag? = this.tag?.get(key)

internal fun ItemStack.computeRawLore(): List<String> {
    val lore = this[DataComponents.LORE] ?: return emptyList()
    return lore.lines().map { it.stripped }
}

// TODO: make this less strict, some items in GUIs are actually glass panes
public fun ItemStack.isSkyblockFiller(): Boolean = isEmpty || this in ItemTag.GLASS_PANES

public fun ItemStack.getLore(): List<Component> = this[DataComponents.LORE]?.lines() ?: emptyList()

public fun ItemStack.getRawLore(): List<String> = this.getData(DataTypes.RAW_LORE) ?: this.computeRawLore()
public val ItemStack.cleanName: String get() = this.getData(DataTypes.CLEAN_NAME) ?: this.hoverName.stripped

public fun ItemStack.isSameItem(other: ItemStack?): Boolean {
    if (other == null) return false
    return this == other || ItemStack.isSameItemSameComponents(this, other)
}

public fun ItemStack.getRarityLineIndex(): Int {
    val rarity = this.getData(DataTypes.RARITY) ?: return -1
    val rarityName = rarity.displayName.uppercase()
    val lore = getRawLore()
    return lore.indexOfLast { it.contains(rarityName) }
}

public fun ItemStack.getTexture(): String? {
    val skin = this.get(DataComponents.PROFILE) ?: return null
    return skin.properties.get("textures").firstOrNull()?.value()
}

public fun ItemStack(item: Item, builder: ItemStack.() -> Unit): ItemStack {
    val stack = ItemStack(item)
    stack.builder()
    return stack
}

public operator fun Item.contains(item: ItemStack): Boolean = item.item == this

public operator fun <T : Any> ItemBuilder.set(type: DataComponentType<T>, value: T) = this.set(type, value)
public operator fun <T : Any> ItemStack.get(type: DataComponentType<T>): T? = this.get(type)
public operator fun <T : Any> ItemStack.set(type: DataComponentType<T>, value: T): T? = this.set(type, value)
public operator fun <T> ItemStack.get(type: DataType<T>) = this.getData(type)

public fun ItemStack.getSkyBlockId() = getData(DataTypes.ID)
public fun ItemStack.getApiId() = getData(DataTypes.API_ID)
public fun ItemStack.getItemModel(): Item = getData(DataTypes.VISIBLE_ITEM) ?: item

public val Item.holder: Holder<Item> get() = this.builtInRegistryHolder()

public fun List<Slot>.filterContainerSlots() = this.filterNot { it.container is Inventory }
public fun List<Slot>.filterContainerItems() = this.filterContainerSlots().map { it.item }

public fun createSkull(textureBase64: String): ItemStack {
    return createSkull(
        GameProfile {
            put("textures", Property("textures", textureBase64))
        },
    )
}

public fun createSkull(profile: GameProfile): ItemStack {
    val stack = ItemStack(Items.PLAYER_HEAD)
    stack.set(DataComponents.PROFILE, profile.toResolvableProfile())
    return stack
}
