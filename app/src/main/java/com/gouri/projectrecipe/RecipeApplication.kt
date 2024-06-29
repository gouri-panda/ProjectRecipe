package com.gouri.projectrecipe

import android.app.Application
import com.gouri.projectrecipe.networking.RetrofitClient
import com.gouri.projectrecipe.networking.SpoonacularApi
import com.gouri.projectrecipe.room.RecipeDatabase

class RecipeApplication : Application() {
    val database by lazy { RecipeDatabase.getDatabase(this) }
    val repository by lazy { RecipeRepository(RetrofitClient.instance.create(SpoonacularApi::class.java), database.recipeDao()) }
}
