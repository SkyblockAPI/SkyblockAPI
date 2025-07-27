package tech.thatgravyboat.skyblockapi.utils.extentions

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.*

@Suppress("DEPRECATION")
val ItemStack.tag: CompoundTag? get() = this[DataComponents.CUSTOM_DATA]?.unsafe
fun ItemStack.getTag(key: String): Tag? = this.tag?.get(key)

fun ItemStack.getRawLore(): List<String> {
    val lore = this[DataComponents.LORE] ?: return emptyList()
    return lore.lines().map { it.stripped }
}

fun ItemStack.isSkyblockFiller(): Boolean = isEmpty || this in ItemTag.GLASS_PANES

fun ItemStack.getLore(): List<Component> = this[DataComponents.LORE]?.lines() ?: emptyList()

val ItemStack.cleanName: String get() = hoverName.stripped

fun ItemStack.isSameItem(other: ItemStack?): Boolean {
    if (other == null) return false
    return this == other || ItemStack.isSameItemSameComponents(this, other)
}

fun ItemStack.getRarityLineIndex(): Int {
    val rarity = this.getData(DataTypes.RARITY) ?: return -1
    val rarityName = rarity.displayName.uppercase()
    val lore = getRawLore()
    return lore.indexOfLast { it.contains(rarityName) }
}

fun ItemStack.getTexture(): String? {
    val skin = this.get(DataComponents.PROFILE) ?: return null
    return skin.gameProfile().properties.get("textures").first().value()
}

fun ItemStack(item: Item, builder: ItemStack.() -> Unit): ItemStack {
    val stack = ItemStack(item)
    stack.builder()
    return stack
}

operator fun Item.contains(item: ItemStack): Boolean = item.item == this

operator fun <T> ItemBuilder.set(type: DataComponentType<T>, value: T) = this.set(type, value)
operator fun <T> ItemStack.get(type: DataComponentType<T>): T? = this.get(type)
operator fun <T> ItemStack.set(type: DataComponentType<T>, value: T) = this.set(type, value)

fun ItemStack.getSkyBlockId() = getData(DataTypes.ID)
fun ItemStack.getApiId() = getData(DataTypes.API_ID)
fun ItemStack.getItemModel(): Item = getData(DataTypes.VISIBLE_ITEM) ?: item

val Item.holder: Holder<Item> get() = this.builtInRegistryHolder()

fun createSkull(textureBase64: String): ItemStack {
    val profile = GameProfile(UUID.randomUUID(), "a")
    profile.properties.put("textures", Property("textures", textureBase64))
    return createSkull(profile)
}

fun createSkull(profile: GameProfile): ItemStack {
    val stack = ItemStack(Items.PLAYER_HEAD)
    stack.set(DataComponents.PROFILE, ResolvableProfile(profile))
    return stack
}

@RemoveNextVersion
object ItemUtils {

    fun createSkull(textureBase64: String): ItemStack {
        val profile = GameProfile(UUID.randomUUID(), "a")
        profile.properties.put("textures", Property("textures", textureBase64))
        return createSkull(profile)
    }

    fun createSkull(profile: GameProfile): ItemStack {
        val stack = ItemStack(Items.PLAYER_HEAD)
        stack.set(DataComponents.PROFILE, ResolvableProfile(profile))
        return stack
    }
}
