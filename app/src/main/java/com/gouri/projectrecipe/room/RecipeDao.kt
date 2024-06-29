package com.gouri.projectrecipe.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract interface RecipeDao {
    @Query("SELECT * FROM recipes")
    abstract fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract  fun insertRecipe(recipe: RecipeEntity)

    @Delete
    abstract fun deleteRecipe(recipe: RecipeEntity)
}
