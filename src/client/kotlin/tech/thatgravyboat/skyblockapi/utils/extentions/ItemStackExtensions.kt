package tech.thatgravyboat.skyblockapi.utils.extentions

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.*

@Suppress("DEPRECATION")
val ItemStack.tag: CompoundTag? get() = this[DataComponents.CUSTOM_DATA]?.unsafe
fun ItemStack.getTag(key: String): Tag? = this.tag?.get(key)

fun ItemStack.getRawLore(): List<String> {
    val lore = this[DataComponents.LORE] ?: return emptyList()
    return lore.lines.map { it.stripped }
}

fun ItemStack.getLore(): List<Component> = this[DataComponents.LORE]?.lines ?: emptyList()

val ItemStack.cleanName: String get() = hoverName.stripped

fun ItemStack.isSameItem(other: ItemStack?): Boolean {
    if (other == null) return false
    return this == other || ItemStack.isSameItemSameComponents(this, other)
}

fun ItemStack.getTexture(): String? {
    val skin = this.get(DataComponents.PROFILE) ?: return null
    return skin.gameProfile.properties.get("textures").first().value
}

fun ItemStack(item: Item, builder: ItemStack.() -> Unit): ItemStack {
    val stack = ItemStack(item)
    stack.builder()
    return stack
}

fun ItemStack.getId() = getData(DataTypes.ID)

val Item.holder: Holder<Item> get() = this.builtInRegistryHolder()

@Deprecated("")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6")
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
