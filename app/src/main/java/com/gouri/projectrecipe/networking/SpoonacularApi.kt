package com.gouri.projectrecipe.networking

import com.gouri.projectrecipe.RecipeResponse
import com.gouri.projectrecipe.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int
    ): RecipeResponse

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String
    ): SearchResponse
}
