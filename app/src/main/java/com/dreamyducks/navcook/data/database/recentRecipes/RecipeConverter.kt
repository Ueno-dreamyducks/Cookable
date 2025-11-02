package com.dreamyducks.navcook.data.database.recentRecipes

import androidx.room.TypeConverter
import com.dreamyducks.navcook.network.Ingredient
import com.dreamyducks.navcook.network.Step
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipeConverter {
    @TypeConverter
    fun fromIngredients(ingredients: List<Ingredient>?) : String? {
        return ingredients.let { Gson().toJson(it) }
    }
    @TypeConverter
    fun toIngredients(value: String?) : List<Ingredient>? {
        if(value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromSteps(steps: List<Step>?) : String? {
        return steps.let { Gson().toJson(it) }
    }
    @TypeConverter
    fun toSteps(value: String?) : List<Step>? {
        if(value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<Step>>() {}.type
        return Gson().fromJson(value, listType)
    }
}