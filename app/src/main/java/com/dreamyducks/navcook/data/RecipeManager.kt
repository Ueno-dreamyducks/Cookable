package com.dreamyducks.navcook.data

import com.dreamyducks.navcook.network.Recipe
import com.dreamyducks.navcook.network.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RecipeManager {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery : StateFlow<String> = _searchQuery.asStateFlow()

    private val _foundRecipes = MutableStateFlow<List<SearchResult>>(listOf(SearchResult()))
    val foundRecipes: StateFlow<List<SearchResult>> = _foundRecipes.asStateFlow()
    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }
    fun updateFoundRecipes(recipes: List<SearchResult>) {
        _foundRecipes.value = recipes
    }

    fun updateSelectedRecipe(newSelect: Recipe) {
        _selectedRecipe.value = newSelect
    }
}