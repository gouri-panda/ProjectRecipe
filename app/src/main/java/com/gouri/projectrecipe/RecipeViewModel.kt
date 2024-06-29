package com.gouri.projectrecipe

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    val searchQuery = mutableStateOf("")
    val recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val favoriteRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val searchResults = MutableStateFlow<List<Recipe>>(emptyList())

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        searchRecipes(query)
    }

    fun selectRecipe(recipe: Recipe) {
        // Navigate to detail screen
    }

    fun getRecipeById(id: String?): Recipe? {
        // Fetch recipe by ID
        return recipes.value.find { it.id.toString() == id }
    }

    private fun searchRecipes(query: String) {
        viewModelScope.launch {
            repository.searchRecipes("YOUR_API_KEY", query).collect {
                searchResults.value = it
            }
        }
    }
}
