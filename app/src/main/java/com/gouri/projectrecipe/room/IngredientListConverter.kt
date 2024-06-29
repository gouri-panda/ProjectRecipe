package com.gouri.projectrecipe.room

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gouri.projectrecipe.Ingredient

class IngredientListConverter {
    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>): String {
        val gson = Gson()
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun toIngredientList(data: String): List<Ingredient> {
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        val gson = Gson()
        return gson.fromJson(data, listType)
    }
}