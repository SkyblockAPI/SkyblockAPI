package tech.thatgravyboat.skyblockapi.api.remote

import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.ResolvableProfile
import tech.thatgravyboat.repolib.api.PetsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.extensions.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import java.util.*

object RepoPetsAPI {

    private val cache: MutableMap<PetQuery, ItemStack?> = mutableMapOf()

    fun getPetInfo(id: String): PetsAPI.Data? {
        return RepoAPI.pets().getPet(id)
    }

    fun getPetAsItemOrNull(query: PetQuery): ItemStack? = cache.getOrPut(query) {
        val data = RepoAPI.pets().getPet(query.id)
        val pet = data.tiers()[query.rarity.name]
        val skin = query.skin?.let { RepoItemsAPI.getItem("PET_SKIN_$it") }

        if (pet == null) return@getOrPut null

        val base = skin ?: ItemStack(Items.PLAYER_HEAD) {
            this[DataComponents.PROFILE] = ResolvableProfile(
                Optional.empty(),
                Optional.empty(),
                PropertyMap().apply { put("textures", Property("textures", pet.texture())) },
            )
        }

        base[DataComponents.CUSTOM_NAME] = getFormattedName(
            data.name(),
            query.level,
            query.rarity,
            query.skin != null,
            skin?.getData(DataTypes.RARITY),
        )
        base[DataComponents.LORE] = ItemLore(pet.getFormattedLore(query.level, query.heldItem).map(Text::of))

        return@getOrPut base
    }

    fun getPetAsItem(id: String, rarity: SkyBlockRarity, level: Int = 100, skin: String? = null, heldItem: String? = null) =
        getPetAsItem(
            PetQuery(id, rarity, level, skin, heldItem),
        )

    fun getPetAsItem(query: PetQuery): ItemStack = getPetAsItemOrNull(query) ?: ItemStack(Items.BARRIER) {
        this[DataComponents.ITEM_NAME] = getFormattedName(query.id, query.level, query.rarity, query.skin != null, null)
    }

    private fun getFormattedName(
        name: String,
        level: Int,
        rarity: SkyBlockRarity,
        hasSkin: Boolean,
        skinRarity: SkyBlockRarity?,
    ) = Text.join(
        Text.of("[Lvl $level] ") { this.color = TextColor.GRAY },
        Text.of(name) { this.color = rarity.color },
        if (hasSkin) {
            Text.of(" ✦") { this.color = skinRarity?.color ?: TextColor.LIGHT_PURPLE }
        } else null,
    ) {
        this.italic = false
    }
}

data class PetQuery(
    val id: String,
    val rarity: SkyBlockRarity,
    val level: Int,
    val skin: String? = null,
    val heldItem: String? = null,
)
