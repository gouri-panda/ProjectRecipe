package com.gouri.projectrecipe

data class RecipeResponse(val recipes: List<Recipe>)
data class SearchResponse(val results: List<Recipe>)
data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String,
    val extendedIngredients: List<Ingredient>
)
data class Ingredient(val name: String, val amount: Double, val unit: String)
