package com.dreamyducks.navcook.data

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RecipeRepository {
    init {
        Log.d("DataRepository", "New DataRepository instance created: ${this.hashCode()}")
    }

    private val _foundRecipes = MutableStateFlow<List<Recipe>>(listOf())
    val foundRecipes: StateFlow<List<Recipe?>> = _foundRecipes.asStateFlow()
    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

    fun updateFoundRecipes(recipes: List<Recipe>) {
        _foundRecipes.value = recipes
    }

    fun updateSelectedRecipe(newSelect: Recipe) {
        _selectedRecipe.value = newSelect
    }
}