package com.gouri.projectrecipe.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gouri.projectrecipe.Recipe

@Database(entities = [RecipeEntity::class], version = 1)
@TypeConverters(IngredientListConverter::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
