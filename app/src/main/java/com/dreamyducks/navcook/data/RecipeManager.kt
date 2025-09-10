package com.dreamyducks.navcook.data

import android.util.Log
import com.dreamyducks.navcook.network.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RecipeManager {
    init {
        Log.d("DataRepository", "New DataRepository instance created: ${this.hashCode()}")
    }

    private val _foundRecipes = MutableStateFlow<List<SearchResult>>(listOf(SearchResult()))
    val foundRecipes: StateFlow<List<SearchResult>> = _foundRecipes.asStateFlow()
    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

    fun updateFoundRecipes(recipes: List<SearchResult>) {
        _foundRecipes.value = recipes
    }

    fun updateSelectedRecipe(newSelect: Recipe) {
        _selectedRecipe.value = newSelect
    }
}