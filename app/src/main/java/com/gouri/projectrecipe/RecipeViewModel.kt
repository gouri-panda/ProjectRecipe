package com.gouri.projectrecipe

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gouri.projectrecipe.room.RecipeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    val searchQuery = mutableStateOf("")
    val recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val searchResults = MutableStateFlow<List<Recipe>>(emptyList())

    init {
//        randomRecipes()
    }
    val favoriteRecipes = repository.allRecipes.map { recipes ->
        recipes.map { recipeEntity ->
            Recipe(
                recipeEntity.id,
                recipeEntity.title,
                recipeEntity.image,
                recipeEntity.instructions,
                recipeEntity.extendedIngredients,
                recipeEntity.favorite
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        searchRecipes(query)
    }
    private var navigateToDetail: ((Int) -> Unit)? = null

    fun setNavigationFunction(navigateToDetail: (Int) -> Unit) {
        this.navigateToDetail = navigateToDetail
    }

    fun selectRecipe(recipe: Recipe) {
        // Navigate to detail screen
        navigateToDetail?.invoke(recipe.id)
    }

    fun getRecipeById(id: String?): Recipe? {
        // Fetch recipe by ID
        return recipes.value.find { it.id.toString() == id }
    }

    private fun searchRecipes(query: String) {
        viewModelScope.launch {
            repository.searchRecipes("c051581d9185437abc44bf07a7c496b2", query).collect {
                Log.d("RecipeViewModel", "Search results: $it")
                searchResults.value = it
                recipes.value = it
            }
        }
    }
    private fun randomRecipes() {
        viewModelScope.launch {
            repository.randomRecipes("c051581d9185437abc44bf07a7c496b2", 15).collect {
                Log.d("RecipeViewModel", "Search results(Random): $it")
                recipes.value = it
            }.runCatching {
                Log.d("RecipeViewModel", "Error fetching random recipes:")
            }
        }
    }
    fun toggleFavoriteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val isFavorite = favoriteRecipes.value.any { it.id == recipe.id }
            Log.d("RecipeViewModel", "isFavorite: $isFavorite")
            // Toggle favorite state
            recipe.favorite = !isFavorite
            viewModelScope.launch(Dispatchers.IO) {
                if (isFavorite) {
                    repository.deleteFavoriteRecipe(RecipeEntity(id = recipe.id, title = recipe.title, image = recipe.image, instructions = recipe.instructions, extendedIngredients = recipe.extendedIngredients, favorite = false))
                } else {
                    repository.insertFavoriteRecipe(RecipeEntity(id = recipe.id, title = recipe.title, image = recipe.image, instructions = recipe.instructions, extendedIngredients = recipe.extendedIngredients, favorite = true))
                }
            }

        }
    }

}
