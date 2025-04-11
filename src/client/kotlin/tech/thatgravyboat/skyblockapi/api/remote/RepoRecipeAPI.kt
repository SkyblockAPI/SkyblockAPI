package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ingredient.ItemIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.PetIngredient

object RepoRecipeAPI {

    private val forgeRecipeCache: MutableMap<String, ForgeRecipe?> = mutableMapOf()
    private val craftingRecipeCache: MutableMap<String, CraftingRecipe?> = mutableMapOf()

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
}
