package com.gouri.projectrecipe

import com.gouri.projectrecipe.networking.SpoonacularApi
import com.gouri.projectrecipe.room.RecipeDao
import com.gouri.projectrecipe.room.RecipeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RecipeRepository(private val api: SpoonacularApi, private val recipeDao: RecipeDao) {

    val allRecipes: Flow<List<RecipeEntity>> = recipeDao.getAllRecipes()

    fun randomRecipes(apiKey: String, number: Int): Flow<List<Recipe>> = flow {
        val response = api.getRandomRecipes(apiKey, number)
        emit(response.recipes)
    }

    fun searchRecipes(apiKey: String, query: String): Flow<List<Recipe>> = flow {
        val response = api.searchRecipes(apiKey, query)
        emit(response.results)
    }


    fun getAllFavoriteRecipes(): Flow<List<RecipeEntity>> {
        return recipeDao.getAllRecipes()
    }

    suspend fun insertFavoriteRecipe(recipe: RecipeEntity) {
        recipeDao.insertRecipe(recipe)
    }

    suspend fun deleteFavoriteRecipe(recipe: RecipeEntity) {
        recipeDao.deleteRecipe(recipe)
    }
}
