package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ingredient.*

object RepoRecipeAPI {

    private val forgeRecipeCache: MutableMap<String, ForgeRecipe?> = mutableMapOf()
    private val craftingRecipeCache: MutableMap<String, CraftingRecipe?> = mutableMapOf()

    fun getCraftingRecipe(id: String): CraftingRecipe? {
        if (!RepoAPI.isInitialized()) return null
        return craftingRecipeCache.getOrPut(id) {
            RepoAPI.recipes().getRecipes(Recipe.Type.CRAFTING).find { it.result().id() == id }
        }
    }

    fun getForgeRecipe(id: String): ForgeRecipe? {
        if (!RepoAPI.isInitialized()) return null
        return forgeRecipeCache.getOrPut(id) {
            RepoAPI.recipes().getRecipes(Recipe.Type.FORGE).find { it.result().id() == id }
        }
    }
}

fun CraftingIngredient.id(): String? = when (this) {
    is ItemIngredient -> this.id()
    is PetIngredient -> this.id()
    is AttributeIngredient -> this.id()
    is EnchantmentIngredient -> this.id()
    else -> null
}
