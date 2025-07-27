package tech.thatgravyboat.skyblockapi.api.remote

import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.ResolvableProfile
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RunesAPI.Rune
import tech.thatgravyboat.skyblockapi.utils.text.Text
import java.util.*

object RepoRunesAPI {

    private val cache: MutableMap<String, ItemStack?> = mutableMapOf()

    fun getRuneById(id: String) = RepoAPI.runes().getRunes(id)
    fun getRune(id: String, tier: Int) = RepoAPI.runes().getRunes(id).find { it.tier() == tier }

    fun getRune(string: String): Rune? {
        val split = string.split(":")
        if (split.size != 3) return null
        if (split[0] != "rune") return null
        val id = split[1]
        val tier = split[2].toIntOrNull() ?: return null
        return getRune(id, tier)
    }

    @JvmOverloads
    fun getRuneAsItemOrNull(id: String, tier: Int? = null): ItemStack? = cache.getOrPut("$id:$tier") {
        val rune = if (tier == null) {
            getRuneById(id).maxByOrNull(Rune::tier)
        } else {
            getRune(id, tier)
        } ?: return@getOrPut null

        val item = ItemStack(Items.PLAYER_HEAD)
        item[DataComponents.PROFILE] = ResolvableProfile(
            Optional.empty(),
            Optional.empty(),
            PropertyMap().apply { put("textures", Property("textures", rune.texture())) },
        )
        item[DataComponents.CUSTOM_NAME] = Text.of(rune.name())
        item[DataComponents.LORE] = ItemLore(rune.lore().map(Text::of))

        return@getOrPut item
    }

    fun getRuneAsItem(id: String, tier: Int) = getRuneAsItemOrNull(id, tier) ?: ItemStack(Items.BARRIER).apply {
        this[DataComponents.ITEM_NAME] = Text.of("Unknown Rune: $id:$tier")
    }

    fun Rune.getId() = buildString {
        append("rune:")
        append(this@getId.id())
        append(":")
        append(this@getId.tier())
    }

    fun Rune.getItem(): ItemStack? = getRuneAsItem(this.id(), this.tier())
}
