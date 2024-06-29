package com.gouri.projectrecipe

import com.gouri.projectrecipe.networking.SpoonacularApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RecipeRepository(private val api: SpoonacularApi) {
    fun getRandomRecipes(apiKey: String, number: Int): Flow<List<Recipe>> = flow {
        val response = api.getRandomRecipes(apiKey, number)
        emit(response.recipes)
    }

    fun searchRecipes(apiKey: String, query: String): Flow<List<Recipe>> = flow {
        val response = api.searchRecipes(apiKey, query)
        emit(response.results)
    }
}
