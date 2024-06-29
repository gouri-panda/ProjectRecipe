package com.gouri.projectrecipe.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gouri.projectrecipe.Ingredient

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val image: String,
    val instructions: String,
    val extendedIngredients: List<Ingredient>,
    val favorite: Boolean = false
)

