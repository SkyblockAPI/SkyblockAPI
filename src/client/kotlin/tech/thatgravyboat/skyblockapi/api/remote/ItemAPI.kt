package tech.thatgravyboat.skyblockapi.api.remote

import com.mojang.serialization.JsonOps
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoVersion
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ingredient.ItemIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.PetIngredient
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.ItemUtils
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

@Module
object ItemAPI {

    private val itemIdToNameCache: MutableMap<String, String> = mutableMapOf()
    private val itemCache: MutableMap<String, ItemStack> = mutableMapOf()
    private val petCache: MutableMap<PetData, ItemStack> = mutableMapOf()
    private val forgeRecipeCache = mutableMapOf<String, ForgeRecipe?>()
    private val craftingRecipeCache = mutableMapOf<String, CraftingRecipe?>()

    init {
        RepoAPI.setup(RepoVersion.V1_21_5)
    }

    // TODO: @sophie hi
    fun getIdByDisplayName(name: String): String? {
        return ""
    }

    fun getItem(id: String): ItemStack = itemCache.getOrPut(id) {
        val id = (id.takeUnless { it == "MUSHROOM_COLLECTION" } ?: "RED_MUSHROOM").replace(":", "-")

        val data = RepoAPI.items().getItem(id)
        ItemStack.CODEC.parse(JsonOps.INSTANCE, data).ifError { Logger.error(it.message()) }.result().orElse(null) ?: fallbackItem(id)
    }

    fun getItemOrNull(id: String) = getItem(id).takeUnless { it.item == Items.BARRIER }

    fun getItemName(id: String): Component = getItem(id).hoverName

    fun getPet(petData: PetData): ItemStack = petCache.getOrPut(petData) {
        val data = RepoAPI.pets().getPet(petData.id)
        val pet = data.tiers[petData.rarity.name]
        val hasSkin = petData.skin != null
        val skin = petData.skin?.let { getItem("PET_SKIN_$it").copy() }

        pet?.let {
            (skin.takeIf { hasSkin } ?: ItemUtils.createSkull(it.texture)).apply {
                val name = Text.join(
                    Text.of("[Lvl ${petData.level}] ").withColor(TextColor.GRAY),
                    Text.of(data.name).withColor(petData.rarity.color),
                    if (hasSkin) Text.of(" ✦").withColor(skin?.getData(DataTypes.RARITY)?.color ?: TextColor.LIGHT_PURPLE) else null,
                )
                name.italic = false
                val lore = pet.getFormattedLore(petData.level, petData.heldItem)
                set(DataComponents.CUSTOM_NAME, name)
                set(DataComponents.LORE, ItemLore(lore.map(Text::of)))
            }
        } ?: fallbackItem(petData.id)
    }

    fun getPet(id: String, rarity: SkyBlockRarity, level: Int = 1, skin: String? = null, heldItem: String? = null): ItemStack =
        getPet(PetData(id, rarity, level, skin, heldItem))

    fun getCraftingRecipe(id: String): CraftingRecipe? = craftingRecipeCache.getOrPut(id) {
        RepoAPI.recipes().getRecipes(Recipe.Type.CRAFTING).find {
            when (it.result) {
                is ItemIngredient -> (it.result as ItemIngredient).id == id
                is PetIngredient -> (it.result as PetIngredient).id == id
                else -> false
            }
        }
    }

    fun getForgeRecipe(id: String): ForgeRecipe? = forgeRecipeCache.getOrPut(id) {
        RepoAPI.recipes().getRecipes(Recipe.Type.FORGE).find {
            when (it.result) {
                is ItemIngredient -> (it.result as ItemIngredient).id == id
                is PetIngredient -> (it.result as PetIngredient).id == id
                else -> false
            }
        }

    }

    fun fallbackItem(id: String): ItemStack = ItemStack(Items.BARRIER).apply { this.set(DataComponents.ITEM_NAME, Text.of(id)) }

    data class PetData(
        val id: String,
        val rarity: SkyBlockRarity,
        val level: Int,
        val skin: String?,
        val heldItem: String?,
    )
}
