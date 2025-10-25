package com.dreamyducks.navcook.ui.recipeOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OverviewViewModel() : ViewModel() {
    private val recipeManager = RecipeManager

    val recipe: StateFlow<Recipe?> = recipeManager.selectedRecipe

    init {
        viewModelScope.launch(Dispatchers.Default) {
            recipeManager.addLoadedRecipes(recipe.value!!)
        }
    }
}

