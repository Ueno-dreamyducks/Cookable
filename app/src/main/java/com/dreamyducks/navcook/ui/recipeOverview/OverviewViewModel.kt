package com.dreamyducks.navcook.ui.recipeOverview

import androidx.lifecycle.ViewModel
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.flow.StateFlow

class OverviewViewModel() : ViewModel() {
    private val recipeManager = RecipeManager

    val recipe: StateFlow<Recipe?> = recipeManager.selectedRecipe
}

